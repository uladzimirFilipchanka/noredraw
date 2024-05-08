package creator.export.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BinaryDiagram implements Diagram {
    private byte[] data;
}
