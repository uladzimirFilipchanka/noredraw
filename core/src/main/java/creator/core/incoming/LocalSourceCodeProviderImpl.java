package creator.core.incoming;


import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalSourceCodeProviderImpl implements SourceCodeProvider {
    @Override
    public Path provideSourceCode(String destination) {
        return Paths.get(destination);
    }
}
