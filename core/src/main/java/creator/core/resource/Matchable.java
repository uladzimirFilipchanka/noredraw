package creator.core.resource;


import creator.core.resource.matcher.Matcher;

public interface Matchable {
    Matcher<String> matcher();
}
