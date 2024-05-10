package noredraw.core.model;

import noredraw.core.model.matcher.Matcher;
import noredraw.core.model.source.Source;
import lombok.*;

import java.util.Map;

@Builder(toBuilder = true)
@ToString(of = {"name", "type"})
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
