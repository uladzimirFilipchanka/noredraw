package creator.core.provider;

import creator.core.model.Relic;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Provider {

    Predicate<Path> getPathPredicates();

    Collection<Relic> provideResources(Stream<Path> filteredStream);
}
