package noredraw;

import noredraw.core.annotation.Named;
import noredraw.core.model.Relic;
import noredraw.core.provider.Provider;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Named("MY_PROVIDER")
public class MyCustomProvider implements Provider {
    @Override
    public Predicate<Path> getPathPredicates() {
        return path -> path.getFileName().toString().equals("FileILike.ext");
    }

    @Override
    public Collection<Relic> provideResources(Stream<Path> stream) {
        return Collections.singletonList(
                Relic.builder()
                        .name("MyRelic")
                        .build()
        );
    }
}
