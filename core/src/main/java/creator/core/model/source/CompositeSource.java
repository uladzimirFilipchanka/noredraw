package creator.core.model.source;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@ToString
@EqualsAndHashCode
@Getter
public class CompositeSource implements Source {
    @Singular
    private List<Source> sources;

    @Override
    public String describe() {
        return sources.stream().map(Source::describe).collect(Collectors.joining("\n"));
    }
}
