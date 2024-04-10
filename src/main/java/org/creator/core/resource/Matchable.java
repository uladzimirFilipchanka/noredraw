package org.creator.core.resource;

import org.hamcrest.Matcher;

public interface Matchable {
    Matcher<String> matcher();
}
