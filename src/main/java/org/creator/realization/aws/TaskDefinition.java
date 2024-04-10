package org.creator.realization.aws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaskDefinition {
    private String family;
    private List<ContainerDefinition> containerDefinitions;
}