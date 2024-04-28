package creator.core.matching.merge;

import creator.core.resource.CompositeSource;
import creator.core.resource.Relic;
import creator.core.resource.SimpleSource;
import creator.core.resource.matcher.Matcher;
import creator.core.resource.matcher.Matchers;

import java.util.List;
import java.util.stream.Collectors;

public class SameNameDifferentSourceFileMergeStrategy implements MergeStrategy {
    @Override
    public boolean mergeable(Relic left, Relic right) {
        if (left == null || right == null) {
            return false;
        }
        return left.getName().equals(right.getName()) &&
                left.getSource() instanceof SimpleSource &&
                right.getSource() instanceof SimpleSource &&
                !((SimpleSource) left.getSource()).getPath().equals(((SimpleSource) right.getSource()).getPath());
    }

    @Override
    public Relic merge(List<Relic> resources) {
        if (resources.isEmpty()) {
            return null;
        }
        if (resources.size() == 1) {
            return resources.get(0);
        }

        Relic.RelicBuilder compoundBuilder = Relic.builder()
                .name(resources.get(0).getName())
                .type(resources.get(0).getType())
                .matcher(buildMatcher(resources))
                .source(builderCompositeSource(resources));

        // TODO: handle duplicate keys
        resources.stream()
                .flatMap(m -> m.getDefinitions().entrySet().stream())
                .forEach(definition -> compoundBuilder.definition(definition.getKey(), definition.getValue()));
        return compoundBuilder.build();
    }

    private static CompositeSource builderCompositeSource(List<Relic> resources) {
        return CompositeSource.builder()
                .sources(resources.stream()
                        .map(Relic::getSource)
                        .toList())
                .build();
    }

    private static Matcher<String> buildMatcher(List<Relic> resources) {
        return Matchers.anyOf(resources.stream()
                .map(Relic::getMatcher)
                .collect(Collectors.toSet()));
    }
}
