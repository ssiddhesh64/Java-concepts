package com.example.concepts.files.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FilesWalkExample {

    public static void main(String[] args) throws IOException {

        Path root = Path.of(".");

        try (Stream<Path> paths = Files.walk(root)) {

            paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.toString().endsWith(".java"))
                    .forEach(System.out::println);
        }
    }
}
