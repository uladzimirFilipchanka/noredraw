package noredraw.core.model.graph;

import noredraw.core.model.Relic;

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
