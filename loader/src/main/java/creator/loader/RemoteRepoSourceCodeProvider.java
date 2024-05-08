package creator.loader;

import creator.core.incoming.SourceCodeProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.eclipse.jgit.util.FileUtils.RECURSIVE;

@Slf4j
public class RemoteRepoSourceCodeProvider implements SourceCodeProvider {
    private final String repoUrl;

    public RemoteRepoSourceCodeProvider(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    @Override
    @SneakyThrows
    public Path provideSourceCode() {
        Path tempDir = Files.createTempDirectory("git-clone-");

        try (Git ignored = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(tempDir.toFile())
                .call()) {
            log.info("Repository cloned successfully into {}", tempDir);
        } catch (GitAPIException e) {
            log.error("Failed to clone repo", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteFolder(tempDir)));

        return tempDir;
    }

    @SneakyThrows
    private void deleteFolder(Path tempDir) {
        FileUtils.delete(tempDir.toFile(), RECURSIVE);
    }
}
