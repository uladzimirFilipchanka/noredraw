package org.creator.realization.docker;

import org.creator.core.provider.Provider;
import org.creator.realization.FilePredicates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DockerProvider implements Provider<DockerImage> {

    Map<Predicate<String>, BiConsumer<String, DockerImage.DockerImageBuilder>> lineMapper = Map.of(
            line -> line.startsWith("FROM"), (line, relic) -> relic.baseImage(line.split(" ")[1]),
            line -> line.startsWith("EXPOSE"), (line, relic) -> relic.port(Integer.valueOf(line.split(" ")[1])),
            line -> line.startsWith("ENTRYPOINT"), (line, relic) -> {
                List<String> entries = Arrays.asList(line.split(" "));
                relic.entryPoint(entries.subList(1, entries.size()));
            }
    );

    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileNamed("Dockerfile");
    }

    @Override
    public Collection<DockerImage> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::processFile)
                .collect(Collectors.toList());
    }

    private DockerImage processFile(Path file) {
        DockerImage.DockerImageBuilder builder = new DockerImage.DockerImageBuilder();

        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> lineMapper.entrySet().stream()
                    .filter(entry -> entry.getKey().test(line))
                    .forEach(entry -> entry.getValue().accept(line, builder)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        builder.path(file);
        return builder.build();
    }
}
