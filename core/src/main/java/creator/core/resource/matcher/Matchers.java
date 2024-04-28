package creator.core.resource.matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Matchers {
    public static FunMatcher.FunMatcherBuilder<String> contains(String value) {
        return FunMatcher.<String>builder()
                .matchTo(value)
                .matchIf(String::contains);
    }

    public static FunMatcher.FunMatcherBuilder<String> equalTo(String value) {
        return FunMatcher.<String>builder()
                .matchTo(value)
                .matchIf(String::equals);
    }

    public static <T> Matcher<T> anyOf(Collection<Matcher<T>> matchers) {
        return new Matcher<>() {
            // TODO: statefull ;(
            private Matcher<T> winner;

            @Override
            public boolean matches(T another) {
                winner = matchers.stream()
                        .filter(matcher -> matcher.matches(another))
                        .findFirst()
                        .orElse(null);

                return winner != null;
            }

            @Override
            public String describe() {
                return winner != null ? winner.describe() : Matcher.super.describe();
            }
        };
    }
}
