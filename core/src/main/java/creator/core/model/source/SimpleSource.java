package creator.core.model.source;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;

@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@Getter
public class SimpleSource implements Source {
    private String name;
    private String providerName;
    private Path path;
    private String relationship;

    @Override
    public String describe() {
        return relationship != null ? relationship : Source.super.describe();
    }
}
