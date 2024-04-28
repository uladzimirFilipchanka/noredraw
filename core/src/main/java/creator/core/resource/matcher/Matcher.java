package creator.core.resource.matcher;

/**
 * Matcher.builder()
 * .matchTo(imageName)
 * .describe("uses")
 * .matchIf((left, right) -> left.equals(right))
 * .build()
 */
public interface Matcher<T> {
    default boolean matches(T another) {
        return false;
    }

    default String describe() {
        return "uses";
    }
}
