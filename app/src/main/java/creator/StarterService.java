package creator;

import creator.core.incoming.LocalSourceCodeProviderImpl;
import creator.core.incoming.SourceCodeProvider;
import creator.core.matching.match.MatchingService;
import creator.core.matching.match.SimpleMatchingService;
import creator.core.matching.merge.MergingService;
import creator.core.matching.merge.StrategyBasedMergingServiceImpl;
import creator.core.matching.model.Graph;
import creator.core.provider.Provider;
import creator.core.resource.Relic;
import creator.export.Diagram;
import creator.export.Exporter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static creator.Configuration.config;

@Slf4j
public class StarterService {
    private final Set<Provider> registry;

    public StarterService() {
        ConfigurationBuilder config = new ConfigurationBuilder().addUrls(ClasspathHelper.forJavaClassPath())
                .addScanners(Scanners.SubTypes);

        registry = new Reflections(config)
                .getSubTypesOf(Provider.class).stream()
                .map(this::createInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.info("Found {} providers", registry.size());
    }

    private Provider createInstance(Class<? extends Provider> clazz) {
        try {
            Constructor<? extends Provider> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            log.error("Can't find default constructor for class {}. Skipping.", clazz);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Can't instantiate class {}. Skipping.", clazz, e);
        }
        return null;
    }

    public void start() {
        SourceCodeProvider sources = new LocalSourceCodeProviderImpl();
        Path path = sources.provideSourceCode(config().getSource());
        log.info("File exist {}, Loading repo from {}", Files.exists(path), path.getFileName().toAbsolutePath());

        List<Relic> resources = registry.stream()
                .map(provider -> processProvider(provider, path))
                .flatMap(Collection::stream)
                .toList();
        log.info("Next resources gathered {}", resources);

        MergingService mergingService = new StrategyBasedMergingServiceImpl(config().getMergeStrategy());
        List<Relic> mergedResources = mergingService.merge(resources);

        MatchingService matchingService = new SimpleMatchingService();
        Graph<Relic> graph = matchingService.match(mergedResources);

        log.info("Resource graph looks like \n{}", graph);

        Exporter exporter = config().getExporter();
        Diagram diagram = exporter.export(graph);

        String output = config().getOutput();
        output = output.contains(File.separator) ? output : config().getSource() + File.separator + output;
        try (FileOutputStream outputStream = new FileOutputStream(output)) {
            outputStream.write(diagram.getData());
            log.info("Diagram serialized successfully into {}", output);
        } catch (IOException e) {
            log.error("Error while exporting to file", e);
        }
    }

    private static Collection<Relic> processProvider(Provider provider, Path path) {
        Predicate<Path> pathPredicates = provider.getPathPredicates();

        Path relativized = path.relativize(path);
        try (Stream<Path> walk = Files.walk(relativized)) {
            Stream<Path> pathStream = walk.filter(pathPredicates);
            return Optional
                    .ofNullable(provider.provideResources(pathStream))
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            log.error("Failure processing provider {}", provider, e);
            throw new RuntimeException(e);
        }
    }
}
