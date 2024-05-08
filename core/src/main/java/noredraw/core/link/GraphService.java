package noredraw.core.link;

import noredraw.core.model.Relic;
import noredraw.core.model.graph.Graph;

import java.util.List;

public interface GraphService {
    Graph<Relic> buildGraph(List<Relic> resources);
}
