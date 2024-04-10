package org.creator.realization.terraform.aws;

import lombok.Builder;
import lombok.Data;
import org.creator.core.resource.ResourceType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Map;

@Data
@Builder
public class AmazonECS implements AWSTerraformResource {
    private String name;

    @Override
    public Matcher<String> matcher() {
        return Matchers.equalTo(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.SERVICE;
    }

    @Override
    public Map<String, String> getDefinitions() {
        return Map.of("name", name);
    }
}
