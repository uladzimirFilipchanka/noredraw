package noredraw;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class ProcessingContext {
    private final Path sourcePath;

    private ProcessingContext(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    private static class Holder {
        private static ProcessingContext INSTANCE;
    }

    public static ProcessingContext processingContext() {
        return Holder.INSTANCE;
    }

    static void initialize(Path initialSourcePath) {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("ProcessingContext has already been initialized.");
        }
        Holder.INSTANCE = new ProcessingContext(initialSourcePath.normalize());
    }

}
