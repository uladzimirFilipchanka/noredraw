package noredraw.providers.ghaction;

import noredraw.core.annotation.Named;
import noredraw.core.model.Relic;
import noredraw.core.model.matcher.Matchers;
import noredraw.core.model.source.SimpleSource;
import noredraw.core.provider.Provider;
import noredraw.providers.FilePredicates;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static noredraw.ProcessingContext.processingContext;

@Named("GITHUB_DOCKER_ACTION")
@Slf4j
public class GithubDockerActionProvider implements Provider {
    // TODO: think about feature flags
    private final static boolean ABBREVIATE_TAGS = false;

    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileWithExtension("yml", "yaml")
                .and(FilePredicates.inFolder(".github"));
    }

    @Override
    public Collection<Relic> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::processFile)
                .collect(Collectors.toList());
    }

    private Relic processFile(Path file) {
        Relic.RelicBuilder builder = Relic.builder();
        builder.type("DockerImage");

        builder.source(SimpleSource.builder()
                .relationship("builds")
                .providerName(this.getClass().getSimpleName())
                .path(file)
                .name(GithubActionConstants.SOURCE_NAME)
                .build()
        );

        Yaml yaml = new Yaml();
        try {
            // TODO: make me less ugly, maybe yaml path?
            Map<String, Object> yamlData = yaml.load(new FileInputStream(file.toFile()));

            ((Map<String, Map<String, Object>>) yamlData.get("jobs")).values().stream()
                    .flatMap(jobItems -> jobItems.entrySet().stream())
                    .filter(jobItem -> "steps".equals(jobItem.getKey()))
                    .flatMap(steps -> ((ArrayList<LinkedHashMap<String, ?>>) steps.getValue()).stream())
                    .filter(step -> step.containsKey("uses") && ((String) step.get("uses")).startsWith("docker/build-push-action"))
                    .forEach(dockerPushStep -> {
                        Map<String, String> with = (Map<String, String>) dockerPushStep.get("with");
                        String[] tags = with.get("tags").split("\n");
                        addTags(builder, tags);

                        String pathToDockerfile = "UNKNOWN";
                        if (with.containsKey("file")) {
                            pathToDockerfile = with.get("file");
                        } else if (with.containsKey("context")) {
                            pathToDockerfile = with.get("context") + "/Dockerfile";
                        }
                        Path normalizedPath = Path.of(pathToDockerfile).normalize();
                        if (!Files.exists(processingContext().getSourcePath().resolve(Path.of(pathToDockerfile)))) {
                            log.warn("Can't locate path to Dockerfile {} in repo", pathToDockerfile);
                        }
                        String path = normalizedPath.toString();
                        builder.name(buildName(tags));
                        builder.definition("path", path);
                        builder.matcher(Matchers.equalsTo(path)
                                .relationship("creates")
                                .build());
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }

    private void addTags(Relic.RelicBuilder builder, String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            builder.definition("tag" + i, tags[i]);
        }
    }

    private String buildName(String[] tags) {
        return Arrays.stream(tags)
                .findFirst()
                .map(this::maybeAbbreviateTag)
                .orElse("UNKNOWN");
    }

    private String maybeAbbreviateTag(String tag) {
        return ABBREVIATE_TAGS ? StringUtils.substringAfterLast(tag, "/") : tag;
    }
}
