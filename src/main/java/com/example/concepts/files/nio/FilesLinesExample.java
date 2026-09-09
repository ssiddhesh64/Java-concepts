package com.example.concepts.files.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FilesLinesExample {

    public static void main(String[] args) throws IOException {

        Path path = Path.of("textfile.in");

        try (Stream<String> lines = Files.lines(path)) {

            lines
                    .filter(line -> !line.isBlank())
                    .map(String::trim)
                    .forEach(System.out::println);
        }
    }
}