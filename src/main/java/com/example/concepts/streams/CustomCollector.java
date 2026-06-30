package com.example.concepts.streams;

/**
 * CONCEPT TAUGHT: Custom Stream Collectors
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates how to build a custom Collector using Collector.of() for complex stream aggregation.
 * 
 * KEY LESSONS:
 * - Collector.of() requires a supplier, accumulator, combiner, and finisher.
 * - The combiner is critical for parallel streams to merge intermediate results safely.
 */
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class CustomCollector {
    
    public static Collector<String, ?, String> joiningWithPrefixFilter(String prefix, String delim) {
        return Collector.of(
            ArrayList<String>::new, 
            (list, str) -> { if(!str.startsWith(prefix)) list.add(str); }, 
            (list1, list2) -> {
                list1.addAll(list2); 
                return list1;
            }, 
            (list) -> String.join(delim, list)
        );
    }

    public static Collector<String, ?, String> joiningWithPrefixFilterEfficient(String prefix, String delim) {
        return Collector.of(
            () -> new StringJoiner(delim),
            (joiner, str) -> {
                if (str != null && !str.startsWith(prefix)) {
                    joiner.add(str);
                }
            },
            StringJoiner::merge,
            StringJoiner::toString
        );
    }

    public static void main(String[] args) {
        String res = Stream.of("apple", "banana", "apricot", "cherry")
        .collect(joiningWithPrefixFilter("ap", "-"));

        String res2 = Stream.of("apple", "banana", "apricot", "cherry")
        .collect(joiningWithPrefixFilterEfficient("ap", "-"));

        System.out.println(res);
        System.out.println(res2);
    }

}