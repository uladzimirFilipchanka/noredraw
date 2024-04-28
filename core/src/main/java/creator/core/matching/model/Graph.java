package creator.core.matching.model;

import creator.core.resource.Relic;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Graph<T> {
    void addVertex(T vertex);

    void addEdge(T source, T destination, String edgeName);

    List<T> getAdjacentVertices(T vertex);

    Map<Relic, String> getNamedEdges(T vertex);

    Set<T> getVertices();
}
