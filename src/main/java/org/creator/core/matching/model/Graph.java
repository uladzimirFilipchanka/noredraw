package org.creator.core.matching.model;

import org.creator.core.resource.Resource;

import java.util.List;
import java.util.Set;

public interface Graph {
    void addVertex(Resource vertex);

    void addEdge(Resource source, Resource destination);

    List<Resource> getAdjacentVertices(Resource vertex);

    Set<Resource> getVertices();
}
