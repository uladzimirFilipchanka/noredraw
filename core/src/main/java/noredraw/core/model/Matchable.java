package noredraw.core.model;


import noredraw.core.model.matcher.Matcher;

public interface Matchable {
    Matcher<String> matcher();
}
