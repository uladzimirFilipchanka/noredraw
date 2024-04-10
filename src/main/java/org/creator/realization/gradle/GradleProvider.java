package org.creator.realization.gradle;

import org.apache.commons.lang3.StringUtils;
import org.creator.core.provider.Provider;
import org.creator.realization.FilePredicates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GradleProvider implements Provider<Jar> {
    @Override
    public Predicate<Path> getPathPredicates() {
        return FilePredicates.regularFileNamed("settings.gradle");
    }

    @Override
    public Collection<Jar> provideResources(Stream<Path> paths) {
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
                    Jar.builder()
                            .name(projectName)
                            .targetPath("build/libs/" + projectName + "*.jar") // version should be added
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
