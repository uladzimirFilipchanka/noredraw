package org.creator.core.incoming;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LocalSourceCodeProviderImpl implements SourceCodeProvider {
    @Override
    public Path provideSourceCode(String destination) {
        return Paths.get(destination);
    }
}
