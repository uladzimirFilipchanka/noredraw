package org.creator.realization.aws;

import lombok.Data;

@Data
public class ContainerDefinition {
    private String name;
    private String image;
}