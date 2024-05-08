package creator.core.link.strategy;

import creator.core.model.Relic;
import creator.core.model.matcher.Matcher;
import jdk.jfr.Name;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Name("DEFINITION_MATCH")
@Slf4j
public class MatchingDefinitionLinkStrategy implements LinkStrategy {
    @Override
    public boolean linkable(Relic left, Relic right) {
        Matcher<String> matcher = right.matcher();
        Map<String, String> definitions = new HashMap<>(left.getDefinitions());
        definitions.put("name", left.getName());
        if (matcher == null) {
            return false;
        }

        return definitions
                .entrySet()
                .stream()
                .filter(definition -> matcher.matches(definition.getValue()))
                .anyMatch(definition -> {
                    log.info("Found matching definition named '{}' between {} and {}", definition.getKey(), left, right);
                    return true;
                });
    }
}
