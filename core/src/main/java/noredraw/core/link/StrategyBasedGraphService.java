package noredraw.core.link;

import noredraw.core.link.strategy.LinkStrategy;
import noredraw.core.model.Relic;
import noredraw.core.model.graph.Graph;
import noredraw.core.model.graph.ResourceGraph;

import java.util.List;

public class StrategyBasedGraphService implements GraphService {
    private final LinkStrategy strategy;

    public StrategyBasedGraphService(LinkStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Graph<Relic> buildGraph(List<Relic> resources) {
        Graph<Relic> graph = new ResourceGraph();
        for (Relic resourceLeft : resources) {
            graph.addVertex(resourceLeft);
            for (Relic resourceRight : resources) {
                if (resourceLeft == resourceRight) {
                    continue;
                }
                graph.addVertex(resourceRight);
                if (strategy.linkable(resourceLeft, resourceRight)) {
                    graph.addEdge(resourceRight, resourceLeft, resourceRight.getMatcher().describe());
                }
            }
        }
        return graph;
    }
}
