package creator.core.model;


import creator.core.model.matcher.Matcher;

public interface Matchable {
    Matcher<String> matcher();
}
