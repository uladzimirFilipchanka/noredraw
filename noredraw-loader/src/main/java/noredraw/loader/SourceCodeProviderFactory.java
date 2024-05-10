package noredraw.loader;

import noredraw.core.incoming.SourceCodeProvider;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class SourceCodeProviderFactory {
    private static final String GIT_URL_REGEX = "^(https?://|git@|git://).*?\\.git$";

    public SourceCodeProvider createProvider(String source) {
        if (isGitRepoUrl(source)) {
            return new RemoteRepoSourceCodeProvider(source);
        } else {
            Path path = Path.of(source);
            if (!Files.isDirectory(path) || Objects.requireNonNull(path.toFile().list()).length == 0) {
                throw new IllegalArgumentException("Provided source [" + path + "] is not a directory or it is empty");
            }
            return new LocalSourceCodeProvider(source);
        }
    }

    private boolean isGitRepoUrl(String url) {
        if (url == null) {
            return false;
        }

        return url.matches(GIT_URL_REGEX);
    }
}
