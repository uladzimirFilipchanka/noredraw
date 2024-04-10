package org.creator.realization;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class FilePredicates {
    public static Predicate<Path> regularFile() {
        return Files::isRegularFile;
    }

    public static Predicate<Path> fileNamed(String filename) {
        return path -> path.getFileName().toString().equals(filename);
    }

    public static Predicate<Path> regularFileNamed(String filename) {
        return FilePredicates.regularFile()
                .and(FilePredicates.fileNamed(filename));
    }

    public static Predicate<Path> extension(String... extensions) {
        return path -> StringUtils.endsWithAny(path.getFileName().toString(), extensions);
    }

    public static Predicate<Path> regularFileWithExtension(String... extensions) {
        return FilePredicates.regularFile()
                .and(FilePredicates.extension(extensions));
    }

    public static Predicate<Path> inFolder(String folderName) {
        return path -> path.getParent() != null && path.getParent().toString().contains(folderName);
    }
}
