package creator.core.incoming;

import java.nio.file.Path;

public interface SourceCodeProvider {
    Path provideSourceCode(String destination);
}
