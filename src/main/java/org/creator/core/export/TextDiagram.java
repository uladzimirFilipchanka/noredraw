package org.creator.core.export;

import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
@Builder
public class TextDiagram implements Diagram {
    private String definition;

    @Override
    public byte[] getData() {
        return definition.getBytes(StandardCharsets.UTF_8);
    }
}
