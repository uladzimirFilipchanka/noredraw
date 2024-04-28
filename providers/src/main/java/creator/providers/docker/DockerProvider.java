package creator.providers.docker;

import creator.core.provider.Provider;
import creator.core.resource.Relic;
import creator.core.resource.SimpleSource;
import creator.core.resource.matcher.FunMatcher;
import creator.providers.FilePredicates;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DockerProvider implements Provider {
    Map<Predicate<String>, BiConsumer<String, Relic.RelicBuilder>> lineMapper = Map.of(
            line -> line.startsWith("FROM"), (line, relic) ->
                    relic.definition("baseImage", line.split(" ")[1]),
            line -> line.startsWith("EXPOSE"), (line, relic) ->
                    relic.definition("port", line.split(" ")[1]),
            line -> line.startsWith("ENTRYPOINT"), (line, relic) -> {
                String entrypoint = StringUtils.substringAfter(line, "ENTRYPOINT ");
                relic.definition("entrypoint", entrypoint);
                relic.matcher(FunMatcher.<String>builder()
                        .matchTo(entrypoint)
                        .matchIf((our, another) -> another != null && entrypoint.contains(another))
                        .relationship("containerize")
                        .build());
            }
    );

    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileNamed("Dockerfile");
    }

    @Override
    public Collection<Relic> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::processFile)
                .collect(Collectors.toList());
    }

    private Relic processFile(Path file) {
        Relic.RelicBuilder builder = Relic.builder();

        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach(line -> lineMapper.entrySet().stream()
                    .filter(entry -> entry.getKey().test(line))
                    .forEach(entry -> entry.getValue().accept(line, builder)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        builder.definition("path", file.toString());
        builder.type("Dockerfile");
        builder.name(file.getFileName().toString());

        builder.source(SimpleSource.builder()
                .providerName(this.getClass().getSimpleName())
                .path(file)
                .name("FileSystem")
                .build()
        );

        return builder.build();
    }
}
