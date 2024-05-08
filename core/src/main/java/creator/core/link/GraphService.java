package creator.core.link;

import creator.core.model.Relic;
import creator.core.model.graph.Graph;

import java.util.List;

public interface GraphService {
    Graph<Relic> buildGraph(List<Relic> resources);
}
