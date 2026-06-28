import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Sealed interface with permits
sealed interface Shape permits Circle, Rectangle {}

// Records are concise and automatically final
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}

// Using pattern matching in switch (Java 17+ / standard in 21)

@FunctionalInterface
interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}

class Employee {
    private final int id;
    private final String name;
    

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Employee e) {
            return this.id == e.id && Objects.equals(this.name, e.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

// or simply
// public record Employee(int id, String name) {}

public class Streams {

    public static double calculateArea(Shape shape) {
        if(shape instanceof Circle c) {
            return Math.PI * c.radius() * c.radius();
        }
        else if(shape instanceof Rectangle r) {
            return r.width() * r.height();
        }
        return 0;

        // Java 17+ / Standard in 21
        // return switch (shape) {
        //     case Circle c -> Math.PI * c.radius() * c.radius();
        //     case Rectangle r -> r.width() * r.height();
        // };
    }
    
    public static String cleanAndConcatenate(List<String> strs) {
        
        return Optional.ofNullable(strs)
            .orElse(List.of())
            .stream()
            .filter(str -> str != null && !str.isBlank())
            .map(String::toUpperCase)
            .collect(Collectors.joining(", "));
    }

    public static <T> void filterAndCopy(List<? extends T> src, List<? super T> dest, Predicate<? super T> pred) {
        // antipattern
        // src.stream().filter(pred).forEach(dest::add);

        dest.addAll(src.stream().filter(pred).toList());
    }

    public static void main(String[] args) {
        
        List<String> list = Arrays.asList("A");

        System.out.println(cleanAndConcatenate(list));

        // Null will throw exception
        TriFunction<String, String, String, String> triFunction = (a, b, c) -> String.join(" ", List.of(a, b, c));

        TriFunction<String, String, String, String> betteTriFunction = (a, b, c) -> String.join(" ", Stream.of(a, b, c).filter(Objects::nonNull).toArray(String[]::new));

        // System.out.println(triFunction.apply("Hello", null, "Test"));
        System.out.println(betteTriFunction.apply("Hello", null, "Test"));
    }
}
