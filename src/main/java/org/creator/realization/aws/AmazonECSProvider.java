package org.creator.realization.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.creator.core.provider.Provider;
import org.creator.realization.FilePredicates;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class AmazonECSProvider implements Provider<AmazonECSTask> {
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileWithExtension("json");
    }

    @Override
    public Collection<AmazonECSTask> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::parseToJson)
                .filter(Objects::nonNull)
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    private AmazonECSTask toResource(TaskDefinition taskDefinition) {
        AmazonECSTask.AmazonECSTaskBuilder builder = AmazonECSTask.builder();
        // TODO: add multi-container support
        builder.imageName(taskDefinition.getContainerDefinitions().stream()
                .findFirst()
                .map(ContainerDefinition::getImage)
                .orElse(null));
        builder.family(taskDefinition.getFamily());
        return builder.build();
    }

    private TaskDefinition parseToJson(Path path) {
        try {
            return mapper.readValue(path.toFile(), TaskDefinition.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse json for file {}", path, e);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
