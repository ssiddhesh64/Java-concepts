import java.util.Collection;

public class CustomFindMax {
    
    // Why Collection<? extends T>?
    // PECS stands for Producer Extends, Consumer Super.
    // In findMax(Collection<? extends T> collection), the collection is a Producer because we are only reading (producing) values from it to find the max. We are not writing to it.
    // By using ? extends T, we make the method much more flexible. For example, if T is Number, you can pass a List<Integer> or a List<Double>. If you had used Collection<T>, Java would only let you pass a Collection<Number> exactly, and passing a List<Integer> would cause a compilation error.
    // 2. Why Comparable<? super T>?
    // The Comparable interface is a Consumer because its compareTo(T other) method consumes another object of type T to perform the comparison.
    // By using ? super T, we allow a class T to be compared using an implementation of Comparable belonging to its superclass.
    // Example: Imagine a class Animal that implements Comparable<Animal>. Then you have class Dog extends Animal. If you call findMax on a Collection<Dog>, T is Dog. Dog inherits Comparable<Animal> from its parent but does not implement Comparable<Dog> directly.
    // If the bound was <T extends Comparable<T>>, the compiler would look for Comparable<Dog> and fail.
    // Since the bound is <T extends Comparable<? super T>>, it successfully matches Comparable<Animal> (since Animal is a supertype of Dog), and compiles perfectly!
    public static <T extends Comparable<? super T>> T findMax(Collection<? extends T> collection) {

        if(collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Collection cannot be null or empty");
        }

        return collection.stream().reduce((a, b) -> a.compareTo(b) > 0 ? a : b).orElse(null);
        
    }


}