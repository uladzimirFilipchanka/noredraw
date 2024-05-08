package creator.providers.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import creator.core.annotation.Named;
import creator.core.model.Relic;
import creator.core.model.matcher.Matchers;
import creator.core.model.source.SimpleSource;
import creator.core.provider.Provider;
import creator.providers.FilePredicates;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named("AWS_ECS")
@Slf4j
public class AmazonECSProvider implements Provider {
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileWithExtension("json");
    }

    @Override
    public Collection<Relic> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::parseToJson)
                .filter(Objects::nonNull)
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    private Relic toResource(TaskDefinition taskDefinition) {
        Relic.RelicBuilder builder = Relic.builder();
        builder.type("AmazonECSTask");
        builder.source(SimpleSource.builder()
                .providerName(this.getClass().getSimpleName())
                .path(taskDefinition.getPathToTask())
                .name("FileSystem")
                .build()
        );

        // TODO: add multi-container support
        String imageName = taskDefinition.getContainerDefinitions().stream()
                .findFirst()
                .map(ContainerDefinition::getImage)
                .orElse(null);

        builder.definition("image", imageName);
        builder.definition("family", taskDefinition.getFamily());
        builder.name(taskDefinition.getFamily());
        builder.matcher(Matchers.equalsTo(imageName)
                .relationship("assigns")
                .build());
        return builder.build();
    }

    private TaskDefinition parseToJson(Path path) {
        try {
            TaskDefinition taskDefinition = mapper.readValue(path.toFile(), TaskDefinition.class);
            taskDefinition.setPathToTask(path);
            return taskDefinition;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse json for file {}", path, e);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
