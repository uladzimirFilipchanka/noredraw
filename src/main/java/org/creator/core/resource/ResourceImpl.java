package org.creator.core.resource;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Map;

@Builder
@Getter
public class ResourceImpl implements Resource {
    @Singular
    private Map<String, String> definitions;
    private Matcher<String> matcher;
    private String name;
    private String provider;

    @Override
    public Matcher<String> matcher() {
        return matcher != null ? matcher : NEVER_MATCH_MATCHER;
    }

    // TODO: remove this?
    @Override
    public ResourceType getType() {
        return ResourceType.RELIC;
    }

    public static final BaseMatcher<String> NEVER_MATCH_MATCHER = new BaseMatcher<>() {
        @Override
        public boolean matches(Object actual) {
            return false;
        }

        @Override
        public void describeTo(Description description) {

        }
    };
}
