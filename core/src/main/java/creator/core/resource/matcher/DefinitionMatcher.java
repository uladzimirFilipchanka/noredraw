package creator.core.resource.matcher;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import creator.core.resource.Relic;

import java.util.Map;
import java.util.function.BiPredicate;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@Slf4j
public class DefinitionMatcher implements Matcher<String> {
    @NonNull
    private BiPredicate<String, String> matchIf;
    @NonNull
    private String relationship;
    @NonNull
    private String matchToDefinition;
    @NonNull
    private Relic relic;

    @Override
    public boolean matches(String another) {
        Map<String, String> definitions = relic.getDefinitions();
        if (definitions == null || !definitions.containsKey(matchToDefinition)) {
            log.warn("Relic {} doesn't have definition named {}, defaulting to non-match", relic.getName(), matchToDefinition);
        }

        return matchIf.test(definitions.get(matchToDefinition), another);
    }

    @Override
    public String describe() {
        return Matcher.super.describe();
    }
}
