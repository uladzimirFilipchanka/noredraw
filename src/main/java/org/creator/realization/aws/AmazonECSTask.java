package org.creator.realization.aws;

import lombok.Builder;
import lombok.Data;
import org.creator.core.resource.Resource;
import org.creator.core.resource.ResourceType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Map;

@Data
@Builder
public class AmazonECSTask implements Resource {
    private String imageName;
    private String family;

    @Override
    public Matcher<String> matcher() {
        return Matchers.containsString(imageName);
    }

    @Override
    public String getName() {
        return family;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.RELIC;
    }

    @Override
    public Map<String, String> getDefinitions() {
        return Map.of(
                "image", imageName,
                "family", family
        );
    }
}
