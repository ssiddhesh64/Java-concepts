package com.example.concepts.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PrintUtils {

    public static <T> void printList(List<T> list) {
        String output = list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(output);
    }

    public static <T> void printSet(Set<T> set) {
        String output = set.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "{", "}"));
        System.out.println(output);
    }

    public static <K, V> void printMap(Map<K, V> map) {
        String output = map.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
        System.out.println(output);
    }
}
