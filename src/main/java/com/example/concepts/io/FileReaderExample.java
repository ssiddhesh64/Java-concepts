package com.example.concepts.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderExample {

    public static void main(String[] args) {

        String filepath = "textfile.in";
        try(FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr)) {

            String line;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException ex) {
            System.out.println("Unable to read file " + filepath);
            System.out.println("Reason: " + ex.getMessage());
        }
    }
}
