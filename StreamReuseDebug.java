/**
 * CONCEPT TAUGHT: Stream Reuse Limitation
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates that streams cannot be reused once a terminal operation is called.
 * 
 * KEY LESSONS:
 * - Streams are single-use pipelines. Re-evaluating a stream throws IllegalStateException.
 * - To reuse elements, collect them to a list or generate a new stream.
 */
import java.util.*;

public class StreamReuseDebug {
    public static void main(String[] args) {
        List<String> names = List.of("Alice", "Bob", "Charlie");
        long count = names.stream()
        .filter(name -> name.startsWith("A"))
        .count();

        boolean hasAlice = names.stream().anyMatch(name -> name.equals("Alice"));
        
        System.out.println("Count: " + count);
        System.out.println("Has Alice: " + hasAlice);
    }
}
