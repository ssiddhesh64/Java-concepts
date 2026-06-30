package com.example.concepts.solid;

/**
 * CONCEPT TAUGHT: General Problem-Solving Template
 * 
 * WHY THIS WAS WRITTEN:
 * - A clean skeleton class for writing algorithms.
 * 
 * KEY LESSONS:
 * - Structure programs with clean separation of entry point and logic.
 */
import java.util.*;
import java.util.stream.Collectors;

class Solution {

    public static Optional<String> filterNonNull(List<String> strs) {

        String res = strs.stream()
        .filter(s -> Objects.nonNull(s))
        .map(s -> s.toUpperCase())
        .collect(Collectors.joining(","));

        return Optional.of(res);
    }
    public static void main(String[] args) {
        System.out.println("Hello");

        List<String> strs = Arrays.asList("", "abc");

        Optional<String> res = filterNonNull(strs);

        if(res.isPresent()) {
            System.out.println(res.get());
        }
    }

    // Nested helper class to prevent namespace conflicts
    static record Product(String id, String name, double price, List<String> tags) {
    
    public Product {
        if(price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        tags = tags == null ? List.of() : List.copyOf(tags);
    }
    
}

}