package com.example.concepts.files.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesExample {

    public static void main(String[] args) throws IOException {

        Path path = Path.of("textfile.in");

        System.out.println("Exists: " + Files.exists(path));
        System.out.println("Regular file: " + Files.isRegularFile(path));
        System.out.println("Directory: " + Files.isDirectory(path));
        System.out.println("Readable: " + Files.isReadable(path));
        System.out.println("Writable: " + Files.isWritable(path));

        String content = Files.readString(path);

        System.out.println("\n--- File Content ---");
        System.out.println(content);
    }
}
