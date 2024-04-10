package org.creator.realization.terraform.aws;

import com.bertramlabs.plugins.hcl4j.HCLParser;
import com.bertramlabs.plugins.hcl4j.HCLParserException;
import lombok.extern.slf4j.Slf4j;
import org.creator.core.provider.Provider;
import org.creator.realization.FilePredicates;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TerraformAWSProvider implements Provider<AWSTerraformResource> {
    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileWithExtension("tf");
    }

    @Override
    public Collection<AWSTerraformResource> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::parseHcl)
                .filter(Objects::nonNull)
                .flatMap(this::toResources)
                .collect(Collectors.toList());
    }

    private Stream<AmazonECS> toResources(Map<String, Object> terraformMap) {
        AmazonECS.AmazonECSBuilder builder = new AmazonECS.AmazonECSBuilder();

        Map<String, Object> resources = (Map<String, Object>) terraformMap.get("resource");
        return resources.entrySet().stream()
                .filter(e -> "aws_ecs_service".equals(e.getKey()))
                .flatMap(e -> ((Map<String, Map<String, ?>>) e.getValue()).entrySet().stream())
                .filter(e -> "app_service".equals(e.getKey()))
                .map(e -> ((Map<String, Object>) e.getValue()))
                .map(ecs -> builder.name((String) ecs.get("name")).build());
    }

    private Map<String, Object> parseHcl(Path path) {
        try {
            return new HCLParser().parse(path.toFile());
        } catch (HCLParserException e) {
            log.error("Failed to parse HCL for file {}", path, e);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
