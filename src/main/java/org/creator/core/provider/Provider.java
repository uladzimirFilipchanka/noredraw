package org.creator.core.provider;

import org.creator.core.resource.Resource;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Provider<T extends Resource> {

    Predicate<Path> getPathPredicates();
    Collection<T> provideResources(Stream<Path> filteredStream);
}
