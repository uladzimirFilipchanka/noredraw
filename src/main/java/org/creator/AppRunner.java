package org.creator;

import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import lombok.extern.slf4j.Slf4j;
import org.creator.core.export.StructurizerBasedExporter;
import org.creator.core.incoming.LocalSourceCodeProviderImpl;
import org.creator.core.incoming.SourceCodeProvider;
import org.creator.core.matching.model.Graph;
import org.creator.core.matching.service.MatchingService;
import org.creator.core.matching.service.SimpleMatchingService;
import org.creator.core.provider.Provider;
import org.creator.core.resource.Matchable;
import org.creator.core.resource.Resource;
import org.creator.realization.aws.AmazonECSProvider;
import org.creator.realization.docker.DockerProvider;
import org.creator.realization.ghaction.GitHubDockerAction;
import org.creator.realization.gradle.GradleProvider;
import org.creator.realization.terraform.aws.TerraformAWSProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SpringBootApplication
@Slf4j
public class AppRunner {

    private static final Map<String, Provider> registry = Map.of(
            "gradle", new GradleProvider(),
            "docker", new DockerProvider(),
            "gitHubAction", new GitHubDockerAction(),
            "amazonEcsProvider", new AmazonECSProvider(),
            "terraformAWSProvider", new TerraformAWSProvider()
    );

    public static void main(String[] args) {
        SpringApplication.run(AppRunner.class, args);

        SourceCodeProvider sources = new LocalSourceCodeProviderImpl();
        Path path = sources.provideSourceCode("./../arch-diagram-sample");

        List<Resource> resources = registry.values().stream()
                .map(provider -> processProvider(provider, path))
                .flatMap(Collection::stream)
                .toList();
        log.info("Next resources gathered {}", resources);

        MatchingService matchingService = new SimpleMatchingService();
        Graph graph = matchingService.match(resources);

        log.info("Resource graph looks like \n{}", graph);

        StructurizerBasedExporter exporter = new StructurizerBasedExporter(new StructurizrPlantUMLExporter());
        org.creator.core.export.Diagram diagram = exporter.export(graph);
        log.info("Diagram is {}", diagram.getData());

        try (FileOutputStream outputStream = new FileOutputStream("diagram.txt")) {
            outputStream.write(diagram.getData());
            System.out.println("Object serialized successfully.");
        } catch (IOException e) {
            log.error("Error while exporting to file", e);
        }

    }

    private static Collection<? extends Resource> processProvider(Provider<Resource> provider, Path path) {
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
