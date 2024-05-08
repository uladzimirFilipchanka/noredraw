package noredraw.providers.gradle;

import noredraw.core.annotation.Named;
import noredraw.core.model.Relic;
import noredraw.core.model.source.SimpleSource;
import noredraw.core.provider.Provider;
import noredraw.providers.FilePredicates;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Named("GRADLE")
public class GradleProvider implements Provider {
    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileNamed("settings.gradle");
    }

    @Override
    public Collection<Relic> provideResources(Stream<Path> paths) {
        Path settingsGradle = paths.findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find settings.gradle"));

        try {
            String projectName = Files.readAllLines(settingsGradle)
                    .stream()
                    .filter(line -> line.startsWith("rootProject.name"))
                    .findFirst()
                    .map(line -> StringUtils.substringAfter(line, "="))
                    .map(String::trim)
                    .map(line -> StringUtils.strip(line, "'\""))
                    .orElseThrow(() -> new IllegalStateException("Can't find 'rootProject.name' in settings.gradle"));

            return Collections.singletonList(
                    Relic.builder()
                            .name(projectName)
                            .definition("targetPath", "build/libs/" + projectName + "*.jar")
                            .source(
                                    SimpleSource.builder()
                                            .name("Gradle")
                                            .relationship("builds")
                                            .providerName(this.getClass().getSimpleName())
                                            .path(settingsGradle)
                                            .build()
                            )
                            .type("Jar")
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
