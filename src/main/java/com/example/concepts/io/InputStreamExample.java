package com.example.concepts.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamExample {

    public static void main(String[] args) throws IOException {

        InputStream in = System.in;

        System.out.println("Enter text. Press Ctrl+D to send EOF:");

        int value;
        while((value = in.read()) != -1) {
            System.out.println(value);
        }

        System.out.println("EOF Reached!!");
    }
}
