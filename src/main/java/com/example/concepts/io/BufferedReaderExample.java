package com.example.concepts.io;

import java.io.*;
import java.util.StringTokenizer;


//        | Input API                   | EOF indication |
//        | --------------------------- | -------------- |
//        | `BufferedReader.readLine()` | `null`         |
//        | `InputStream.read()`        | `-1`           |
//        | `Scanner.hasNext()`         | `false`        |

//        | Approach                           | Best for                    |  Ease | Speed |
//        | ---------------------------------- | --------------------------- | ----: | ----: |
//        | `Scanner`                          | Simple programs, LLD        | ⭐⭐⭐⭐⭐ |    ⭐⭐ |
//        | `BufferedReader`                   | General text input          |  ⭐⭐⭐⭐ |  ⭐⭐⭐⭐ |
//        | `BufferedReader + StringTokenizer` | Competitive programming     |   ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
//        | Custom `FastScanner`               | Huge input / CP             |    ⭐⭐ | ⭐⭐⭐⭐⭐ |
//        | `Console`                          | Passwords / interactive CLI |  ⭐⭐⭐⭐ |    ⭐⭐ |

public class BufferedReaderExample {

    public static void main(String[] args) throws IOException {

        System.out.println("Working directory: "
                + System.getProperty("user.dir"));

        // Reading from a file
        try (BufferedReader fr = new BufferedReader(new FileReader("textfile.in"))) {

            String line;
            while ((line = fr.readLine()) != null) {
                System.out.println(line);
            }
        }

        // Reading from standard input
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("\nEnter lines. Press Ctrl+D to send EOF:");

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Received: " + line);
            }
        }
    }
}
