package com.example.concepts.streams;

/**
 * CONCEPT TAUGHT: Double Aggregation via Collectors.teeing()
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates double aggregation (finding min and max simultaneously) in a single stream pass.
 * 
 * KEY LESSONS:
 * - Collectors.teeing() runs two collectors on the same stream and merges their results.
 * - Use explicit type witnesses (like Comparator.<Integer>naturalOrder()) to resolve nested compiler type-inference issues.
 */
import java.util.*;
import java.util.stream.Collectors;

public class Teeing {

    public static MinMax findMinMax(List<Integer> numbers) {

        return numbers.stream()
                .collect(Collectors.teeing(
                        Collectors.minBy(Comparator.<Integer>naturalOrder()),
                        Collectors.maxBy(Comparator.<Integer>naturalOrder()),
                        (min, max) -> new MinMax(
                                min.orElse(Integer.MIN_VALUE),
                                max.orElse(Integer.MAX_VALUE))));
    }

    public static void main(String[] args) {
        System.out.println(findMinMax(List.of(1, 2, 3, 4, 5)));
    }

    // Nested helper class to prevent namespace conflicts
    static record MinMax(int min, int max) {
}

}