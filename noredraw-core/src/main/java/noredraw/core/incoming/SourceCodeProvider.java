package noredraw.core.incoming;

import java.nio.file.Path;

public interface SourceCodeProvider {
    Path provideSourceCode();
}
