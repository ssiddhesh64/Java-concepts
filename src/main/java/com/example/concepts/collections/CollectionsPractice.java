package com.example.concepts.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@FunctionalInterface
interface CheckedFunction<T, R, E extends Exception> {
    R apply(T t) throws E;
}

public class CollectionsPractice {

    public static Map<Integer, List<String>> mapLengthToStrings(List<String> strs) {
        if(strs == null) return Map.of();

        return strs.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(String::length));
    }

    public static Optional<String> getValue(Map<String, String> map, String key) {
        return Optional.ofNullable(map)
        .map(m -> m.get(key))
        .filter(s -> !s.isBlank());
    }

    public static Optional<Product> findMostExpensive(List<Product> products) {
        if(products == null) return Optional.empty();
        return products.stream().max(Comparator.comparingDouble(Product::price));
    }

    public static <T> List<T> mergeLists(List<? extends T> list1, List<? extends T> list2) {
        List<T> merged = new ArrayList<>();
        int n = list1.size(), m = list2.size();
        int i = 0, j = 0;
        while(i < n && j < m) {
            merged.add(list1.get(i));
            merged.add(list2.get(j));
            i++;
            j++;
        }

        while(i < n) {
            merged.add(list1.get(i));
            i++;
        }

        while(j < m) {
            merged.add(list2.get(j));
            j++;
        }

        return merged;
    }

    public static <T> List<T> mergeListsProduction(List<? extends T> list1, List<? extends T> list2) {
        if (list1 == null) list1 = List.of();
        if (list2 == null) list2 = List.of();

        List<T> merged = new ArrayList<>(list1.size() + list2.size());
        Iterator<? extends T> it1 = list1.iterator();
        Iterator<? extends T> it2 = list2.iterator();

        while (it1.hasNext() || it2.hasNext()) {
            if (it1.hasNext()) merged.add(it1.next());
            if (it2.hasNext()) merged.add(it2.next());
        }

        return List.copyOf(merged); // Unmodifiable view
    }

    public static <T, R> Function<T, R> wrap(CheckedFunction<T, R, ? extends Exception> checkedFunc) {
        return t -> {
            try {
                return checkedFunc.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }


    public static void main(String[] args) {

        List<String> list = List.of("a", "aa", "aaa", "aa", "aaaa", "b", "bb");
//        ---------------------------------------------------------------------------------
        Map<String, String> map = Map.of("a", "aa", "b", "bbb", "c", "ccc", "d", " ");

        System.out.println(mapLengthToStrings(list));
        System.out.println(getValue(map, "a"));
        System.out.println(getValue(map, "d"));
        System.out.println(getValue(map, "e"));

//        ---------------------------------------------------------------------------------

        List<Product> products = List.of(
            new Product("1", "Product A", 10.0),
            new Product("2", "Product B", 20.0),
            new Product("3", "Product C", 15.0),
            new Product("4", "Product D", 20.0)
        );

        System.out.println(findMostExpensive(products));

//        ---------------------------------------------------------------------------------

        List<Integer> list1 = List.of(1, 2, 3);
        List<Double> list2 = List.of(4.0, 5.0, 6.6, 7.8, 9.9);

        System.out.println(mergeLists(list1, list2));

    }
}

record Product(String id, String name, double price) {

    public Product {
        if(id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null or blank");
        if(name == null || name.isBlank()) throw new IllegalArgumentException("name cannot be null or blank");
        if(price < 0) throw new IllegalArgumentException("price cannot be negative");
    }
}