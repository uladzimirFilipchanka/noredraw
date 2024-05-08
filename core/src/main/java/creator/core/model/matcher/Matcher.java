package creator.core.model.matcher;

public interface Matcher<T> {
    default boolean matches(T another) {
        return false;
    }

    default String describe() {
        return "uses";
    }
}
