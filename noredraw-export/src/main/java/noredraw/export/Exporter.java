package noredraw.export;

import noredraw.core.model.Relic;
import noredraw.core.model.graph.Graph;
import noredraw.export.model.Diagram;

public interface Exporter {
    Diagram export(Graph<Relic> graph, String title);
}
