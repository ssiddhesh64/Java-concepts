package com.example.concepts.solid;

/**
 * CONCEPT TAUGHT: JVM Shutdown Hooks for Resource Cleanup
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates how to clean up temporary directory structures automatically when the JVM exits.
 * 
 * KEY LESSONS:
 * - Runtime.getRuntime().addShutdownHook() registers cleanup actions.
 * - Shutdown hooks must be thread-safe and handle exceptions gracefully.
 */
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class TempDirectory implements AutoCloseable {

    private final Path path;

    public TempDirectory(Path parentDir, String prefix) throws IOException {
        this.path = Files.createTempDirectory(parentDir, prefix);
    }

    public Path getPath() {
        return path;
    }

    @Override
    public void close() {
        try {
            deleteRecursively(path);
        } catch (IOException e) {
            throw new DirectoryCleanupException(
                    "Failed to cleanup temporary directory: " + path,
                    e
            );
        }
    }

    private static void deleteRecursively(Path root)
            throws IOException {

        if (root == null || !Files.exists(root)) {
            return;
        }

        Files.walkFileTree(root, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs)
                    throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    Path dir,
                    IOException exc)
                    throws IOException {

                if (exc != null) {
                    throw exc;
                }

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(
                    Path file,
                    IOException exc)
                    throws IOException {

                throw exc;
            }
        });
    }

    // Nested helper class to prevent namespace conflicts
    static class DirectoryCleanupException extends RuntimeException {
    DirectoryCleanupException(String message) {
        super(message);
    }

    DirectoryCleanupException(String message, Throwable cause) {
        super(message, cause);
    }
}

}