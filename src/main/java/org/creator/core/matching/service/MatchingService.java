package org.creator.core.matching.service;

import org.creator.core.matching.model.Graph;
import org.creator.core.resource.Resource;

import java.util.List;

public interface MatchingService {
    Graph match(List<Resource> resources);
}
