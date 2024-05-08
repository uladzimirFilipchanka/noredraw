package creator;

import creator.core.incoming.SourceCodeProvider;
import creator.core.link.GraphService;
import creator.core.link.StrategyBasedGraphService;
import creator.core.merge.MergingService;
import creator.core.merge.StrategyBasedMergingService;
import creator.core.model.Relic;
import creator.core.model.graph.Graph;
import creator.core.provider.Provider;
import creator.export.Exporter;
import creator.export.model.Diagram;
import creator.loader.SourceCodeProviderFactory;
import creator.utils.NamedClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static creator.Configuration.config;

@Slf4j
public class StarterService {
    private final Map<String, Provider> registry;
    private final SourceCodeProviderFactory sourceCodeProviderFactory;

    public StarterService() {
        sourceCodeProviderFactory = new SourceCodeProviderFactory();

        registry = NamedClassUtils.findNamedClassesOf(Provider.class);
        log.info("Found {} providers: {}", registry.size(), registry.keySet());
    }

    public void start() {
        SourceCodeProvider sourceCodeProvider = sourceCodeProviderFactory.createProvider(config().getSource());
        Path path = sourceCodeProvider.provideSourceCode();

        ProcessingContext.initialize(path);

        List<Relic> resources = registry.values().stream()
                .map(provider -> processProvider(provider, path))
                .flatMap(Collection::stream)
                .toList();
        log.info("Next resources gathered {}", resources);

        MergingService mergingService = new StrategyBasedMergingService(config().getMergeStrategy());
        List<Relic> mergedResources = mergingService.merge(resources);

        GraphService matchingService = new StrategyBasedGraphService(config().getLinkStrategy());
        Graph<Relic> graph = matchingService.buildGraph(mergedResources);

        Exporter exporter = config().getExporter();
        Diagram diagram = exporter.export(graph, getTitle());

        String output = config().getOutput();
        output = output.replaceFirst("^~", System.getProperty("user.home"));
        try (FileOutputStream outputStream = new FileOutputStream(output)) {
            outputStream.write(diagram.getData());
            log.info("Diagram serialized successfully into {}", output);
        } catch (IOException e) {
            log.error("Error while exporting to file", e);
        }
    }

    private String getTitle() {
        return config().getTitle() != null ?
                config().getTitle() :
                StringUtils.capitalize(getBaseFilename(config().getOutput()));
    }

    private String getBaseFilename(String filePath) {
        Path path = Paths.get(filePath);
        String filename = path.getFileName().toString();
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return filename;
        }
        return filename.substring(0, lastDotIndex);
    }

    private Collection<Relic> processProvider(Provider provider, Path path) {
        Predicate<Path> pathPredicates = provider.getPathPredicates();

        try (Stream<Path> walk = Files.walk(path)) {
            Stream<Path> pathStream = walk.filter(pathPredicates);
            return Optional
                    .ofNullable(provider.provideResources(pathStream))
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            log.error("Failure processing provider {}", provider, e);
            return new ArrayList<>();
        }
    }
}
