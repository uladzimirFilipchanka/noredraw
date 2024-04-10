package org.creator.realization.gradle;

import lombok.Builder;
import lombok.Data;
import org.creator.core.resource.Resource;
import org.creator.core.resource.ResourceType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Map;


@Data
@Builder
public class Jar implements Resource {

    private String name;
    private String version;
    private String targetPath;

    @Override
    public ResourceType getType() {
        return ResourceType.RELIC;
    }

    @Override
    public Map<String, String> getDefinitions() {
        return Map.of("name", name);
    }

    @Override
    public Matcher<String> matcher() {
        return new BaseMatcher<>() {
            @Override
            public boolean matches(Object actual) {
                return false;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
