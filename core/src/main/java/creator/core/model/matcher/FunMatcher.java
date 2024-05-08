package creator.core.model.matcher;

import lombok.*;

import java.util.function.BiPredicate;

@Builder
@ToString
@EqualsAndHashCode
@Getter
public class FunMatcher<T> implements Matcher<T> {
    @NonNull
    private BiPredicate<T, T> matchIf;
    private T matchTo;
    @NonNull
    private String relationship;

    @Override
    public boolean matches(T another) {
        return matchIf.test(matchTo, another);
    }

    @Override
    public String describe() {
        return relationship;
    }
}
