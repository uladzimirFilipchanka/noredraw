package creator.export.model;

import lombok.Builder;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
@Builder
public class TextDiagram implements Diagram {
    private String definition;

    @Override
    public byte[] getData() {
        return definition.getBytes(StandardCharsets.UTF_8);
    }
}
