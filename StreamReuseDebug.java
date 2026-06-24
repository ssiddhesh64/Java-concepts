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
