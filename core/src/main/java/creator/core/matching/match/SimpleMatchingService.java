package creator.core.matching.match;

import lombok.extern.slf4j.Slf4j;
import creator.core.matching.model.Graph;
import creator.core.matching.model.ResourceGraph;
import creator.core.resource.Resource;
import creator.core.resource.Relic;
import creator.core.resource.matcher.Matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SimpleMatchingService implements MatchingService {
    @Override
    public Graph<Relic> match(List<Relic> resources) {
        Graph<Relic> graph = new ResourceGraph();
        for (Relic resourceLeft : resources) {
            graph.addVertex(resourceLeft);
            for (Relic resourceRight : resources) {
                if (resourceLeft == resourceRight) {
                    continue;
                }
                graph.addVertex(resourceRight);
                if (doMatch(resourceLeft, resourceRight)) {
                    graph.addEdge(resourceRight, resourceLeft, resourceRight.getMatcher().describe());
                }
            }
        }
        return graph;
    }

    private boolean doMatch(Resource left, Resource right) {
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
