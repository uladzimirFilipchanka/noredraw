package org.creator.core.export;

import org.creator.core.matching.model.Graph;

public interface Exporter {
    Diagram export(Graph graph);
}
