package com.example.concepts.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileOperationsExample {

    public static void main(String[] args) throws IOException {

        Path source = Path.of("textfile.in");
        Path copy = Path.of("textfile-copy.in");
        Path renamed = Path.of("textfile-renamed.in");

        // Copy
        Files.copy(
                source,
                copy,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println("File copied.");

        // Move / rename
        Files.move(
                copy,
                renamed,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println("File moved/renamed.");

        // Delete
        Files.deleteIfExists(renamed);

        System.out.println("File deleted.");
    }
}