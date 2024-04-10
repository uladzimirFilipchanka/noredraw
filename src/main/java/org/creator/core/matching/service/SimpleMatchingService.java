package org.creator.core.matching.service;

import lombok.extern.slf4j.Slf4j;
import org.creator.core.matching.model.Graph;
import org.creator.core.matching.model.ResourceGraph;
import org.creator.core.resource.Resource;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;

@Slf4j
public class SimpleMatchingService implements MatchingService {
    @Override
    public Graph match(List<Resource> resources) {
        Graph graph = new ResourceGraph();
        for (Resource resourceLeft : resources) {
            graph.addVertex(resourceLeft);
            for (Resource resourceRight : resources) {
                if (resourceLeft == resourceRight) {
                    continue;
                }
                graph.addVertex(resourceRight);
                if (doMatch(resourceLeft, resourceRight)) {
                    graph.addEdge(resourceRight, resourceLeft);
                }
            }
        }
        return graph;
    }

    private boolean doMatch(Resource left, Resource right) {
        Matcher<String> matcher = right.matcher();
        Map<String, String> definitions = left.getDefinitions();
        if (definitions == null || matcher == null) {
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
