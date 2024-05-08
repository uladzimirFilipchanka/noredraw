package creator.providers.ghaction;

import creator.ProcessingContext;
import creator.core.annotation.Named;
import creator.core.model.Relic;
import creator.core.model.matcher.Matchers;
import creator.core.model.source.SimpleSource;
import creator.core.provider.Provider;
import creator.providers.FilePredicates;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named("GITHUB_ECS_ACTION")
@Slf4j
public class GithubECSActionProvider implements Provider {
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
        builder.type("AmazonECSTaskDeployment");
        builder.source(SimpleSource.builder()
                .providerName(this.getClass().getSimpleName())
                .relationship("prepares")
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
                    .filter(step -> step.containsKey("uses") && ((String) step.get("uses")).startsWith("aws-actions/amazon-ecs"))
                    .forEach(ecsStep -> {
                        Map<String, String> with = (Map<String, String>) ecsStep.get("with");
                        if (ecsStep.get("uses").toString().startsWith("aws-actions/amazon-ecs-render-task-definition")) {
                            String pathToTask = with.get("task-definition");
                            Path normalizedPath = ProcessingContext.processingContext().getSourcePath().resolve(Path.of(pathToTask)).normalize();
                            if (!Files.exists(normalizedPath)) {
                                log.warn("Can't locate path to ECS task {} in repo", pathToTask);
                            }
                            String normalizedPathString = normalizedPath.toString();
                            builder.matcher(Matchers.equalsTo(normalizedPathString)
                                    .relationship("assigns")
                                    .build());

                            String containerName = with.get("container-name");
                            builder.name(containerName);
                            builder.definition("image", with.get("image"));
                        } else if (ecsStep.get("uses").toString().startsWith("aws-actions/amazon-ecs-deploy-task-definition")) {
                            String service = with.get("service");
                            builder.definition("service", service);

                            builder.definition("cluster", with.get("cluster"));
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }
}
