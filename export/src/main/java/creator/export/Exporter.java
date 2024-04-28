package creator.export;

import creator.core.matching.model.Graph;
import creator.core.resource.Relic;

public interface Exporter {
    Diagram export(Graph<Relic> graph);
}
