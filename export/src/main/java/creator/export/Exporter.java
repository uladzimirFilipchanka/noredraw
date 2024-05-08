package creator.export;

import creator.core.model.Relic;
import creator.core.model.graph.Graph;
import creator.export.model.Diagram;

public interface Exporter {
    Diagram export(Graph<Relic> graph, String title);
}
