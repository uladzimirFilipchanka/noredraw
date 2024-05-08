package creator.loader;


import creator.core.incoming.SourceCodeProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalSourceCodeProvider implements SourceCodeProvider {
    private final String source;

    public LocalSourceCodeProvider(String source) {
        source = source.replaceFirst("^~", System.getProperty("user.home"));
        this.source = source;
    }

    @Override
    public Path provideSourceCode() {
        return Paths.get(source);
    }
}
