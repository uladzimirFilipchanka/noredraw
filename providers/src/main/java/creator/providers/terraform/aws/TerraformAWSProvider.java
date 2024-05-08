package creator.providers.terraform.aws;

import com.bertramlabs.plugins.hcl4j.HCLParser;
import com.bertramlabs.plugins.hcl4j.HCLParserException;
import creator.core.annotation.Named;
import creator.core.model.Relic;
import creator.core.model.matcher.Matchers;
import creator.core.model.source.SimpleSource;
import creator.core.provider.Provider;
import creator.providers.FilePredicates;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named("TERRAFORM_AWS")
@Slf4j
public class TerraformAWSProvider implements Provider {
    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileWithExtension("tf");
    }

    @Override
    public Collection<Relic> provideResources(Stream<Path> filteredStream) {
        return filteredStream
                .map(this::parseHcl)
                .filter(Objects::nonNull)
                .flatMap(this::toResources)
                .collect(Collectors.toList());
    }

    private Stream<Relic> toResources(Pair<Map<String, Object>, Path> pair) {
        Relic.RelicBuilder builder = Relic.builder();
        builder.source(SimpleSource.builder()
                .relationship("provisions")
                .providerName(this.getClass().getSimpleName())
                .path(pair.getRight())
                .name("Terraform")
                .build()
        );
        builder.type("AmazonECS");

        Map<String, Object> resources = (Map<String, Object>) pair.getLeft().get("resource");
        return resources.entrySet().stream()
                .filter(e -> "aws_ecs_service".equals(e.getKey()))
                .flatMap(e -> ((Map<String, Map<String, ?>>) e.getValue()).entrySet().stream())
                .filter(e -> "app_service".equals(e.getKey()))
                .map(e -> ((Map<String, Object>) e.getValue()))
                .map(ecs -> (String) ecs.get("name"))
                .map(name -> builder
                        .name(name)
                        .matcher(Matchers.equalsTo(name).relationship("deploys").build())
                        .build());
    }

    private Pair<Map<String, Object>, Path> parseHcl(Path path) {
        try {
            Map<String, Object> parse = new HCLParser().parse(path.toFile());
            return new ImmutablePair<>(parse, path);
        } catch (HCLParserException e) {
            log.error("Failed to parse HCL for file {}", path, e);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
