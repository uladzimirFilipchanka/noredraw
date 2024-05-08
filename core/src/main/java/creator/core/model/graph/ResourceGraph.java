package creator.core.model.graph;

import creator.core.model.Relic;

import java.util.*;

public class ResourceGraph implements Graph<Relic> {
    private final Map<Relic, Map<Relic, String>> adjacencyList;

    public ResourceGraph() {
        this.adjacencyList = new HashMap<>();
    }

    @Override
    public void addVertex(Relic vertex) {
        adjacencyList.putIfAbsent(vertex, new HashMap<>());
    }

    @Override
    public void addEdge(Relic source, Relic destination, String edgeName) {
        addVertex(source);
        addVertex(destination);
        adjacencyList.get(source).put(destination, edgeName);
    }

    @Override
    public List<Relic> getAdjacentVertices(Relic vertex) {
        return new ArrayList<>(adjacencyList.getOrDefault(vertex, Collections.emptyMap()).keySet());
    }

    @Override
    public Map<Relic, String> getNamedEdges(Relic vertex) {
        return adjacencyList.getOrDefault(vertex, Collections.emptyMap());
    }

    @Override
    public Set<Relic> getVertices() {
        return adjacencyList.keySet();
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<Relic, List<Relic>> entry : adjacencyList.entrySet()) {
//            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
//        }
//        return sb.toString();
//    }
}
