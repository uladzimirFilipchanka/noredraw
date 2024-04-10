package org.creator.core.resource;

import java.util.Map;


public interface Resource extends Matchable {
    String getName();
    ResourceType getType();
    Map<String, String> getDefinitions();
}

