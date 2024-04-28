package creator.core.resource;

import lombok.*;
import creator.core.resource.matcher.Matcher;

import java.util.Map;

@Builder
@ToString
@EqualsAndHashCode
@Getter
public class Relic implements Resource {
    @Singular
    private Map<String, String> definitions;
    private Matcher<String> matcher;
    private String type;
    private String name;
    private Source source;

    @Override
    public Matcher<String> matcher() {
        return matcher != null ? matcher : new Matcher<>() {
        };
    }

    // TODO: remove this?
    @Override
    public String getType() {
        return type;
    }
}
