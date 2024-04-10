package org.creator.realization.ghaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.creator.core.provider.Provider;
import org.creator.realization.FilePredicates;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class GitHubDockerAction implements Provider<DockerHubImage> {
    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileWithExtension("yml", "yaml")
                .and(FilePredicates.inFolder(".github"));
    }

    @Override
    public Collection<DockerHubImage> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::processFile)
                .collect(Collectors.toList());
    }

    private DockerHubImage processFile(Path file) {
        DockerHubImage.DockerHubImageBuilder builder = new DockerHubImage.DockerHubImageBuilder();

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
                        builder.tags(Arrays.stream(with.get("tags").split("\n")).toList());

                        String pathToDockerfile = "UNKNOWN";
                        if (with.containsKey("file")) {
                            pathToDockerfile = with.get("file");
                        } else if (with.containsKey("context")) {
                            pathToDockerfile = with.get("context") + "/Dockerfile";
                        }
                        Path normalizedPath = Path.of(pathToDockerfile).normalize();
                        if (!Files.exists(normalizedPath)) {
                            log.warn("Can't locate path to Dockerfile {} in repo", pathToDockerfile);
                        }
                        builder.path(normalizedPath.toString());
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }
}
