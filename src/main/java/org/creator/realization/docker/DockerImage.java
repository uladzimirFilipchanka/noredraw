package org.creator.realization.docker;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.creator.core.resource.Resource;
import org.creator.core.resource.ResourceType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DockerImage implements Resource {
    private Path path;
    private String baseImage;
    private List<String> entryPoint;
    private Integer port;

    @Override
    public String getName() {
        return path.getFileName().toString();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.RELIC;
    }

    @Override
    public Map<String, String> getDefinitions() {
        return Map.of(
                "baseImage", baseImage,
                "path", path.toString()
        );
    }

    @Override
    public Matcher<String> matcher() {
        return CollectionContainsMatcher.inCollectionContains(entryPoint);
    }

    private static class CollectionContainsMatcher<T> extends BaseMatcher<T> {

        private final Collection<String> collection;

        public CollectionContainsMatcher(Collection<String> collection) {
            this.collection = collection;
        }

        public static <T> Matcher<T> inCollectionContains(Collection<String> collection) {
            return new CollectionContainsMatcher<>(collection);
        }

        @Override
        public boolean matches(Object actual) {
            if (!(actual instanceof String actualString)) {
                return false;
            }
            return collection.stream().anyMatch(element -> element.contains(actualString));
        }

        @Override
        public void describeTo(Description description) {

        }
    }
}
