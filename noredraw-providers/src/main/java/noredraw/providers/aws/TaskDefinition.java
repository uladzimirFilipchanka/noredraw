package noredraw.providers.aws;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.nio.file.Path;
import java.util.List;

@Data
public class TaskDefinition {
    private String family;
    private List<ContainerDefinition> containerDefinitions;
    @JsonIgnore
    private Path pathToTask;
}