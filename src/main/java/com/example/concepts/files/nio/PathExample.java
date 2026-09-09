package com.example.concepts.files.nio;

import java.nio.file.Path;

public class PathExample {

    public static void main(String[] args) {

        Path path = Path.of(
                "src",
                "main",
                "java",
                "com",
                "example"
        );

        System.out.println("Path: " + path);
        System.out.println("Absolute: " + path.toAbsolutePath());
        System.out.println("File name: " + path.getFileName());
        System.out.println("Parent: " + path.getParent());
        System.out.println("Root: " + path.getRoot());
    }
}
