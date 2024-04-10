package org.creator.core.matching.model;

import org.creator.core.resource.Resource;

import java.util.*;

public class ResourceGraph implements Graph {
    private final Map<Resource, List<Resource>> adjacencyList;

    public ResourceGraph() {
        this.adjacencyList = new HashMap<>();
    }

    @Override
    public void addVertex(Resource vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    @Override
    public void addEdge(Resource source, Resource destination) {
        adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
    }

    @Override
    public List<Resource> getAdjacentVertices(Resource vertex) {
        return adjacencyList.getOrDefault(vertex, Collections.emptyList());
    }

    @Override
    public Set<Resource> getVertices() {
        return adjacencyList.keySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Resource, List<Resource>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
