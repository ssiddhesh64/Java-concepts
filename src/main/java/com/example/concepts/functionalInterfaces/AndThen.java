package com.example.concepts.functionalInterfaces;

import java.util.function.Function;

public class AndThen {

//
    static  <T, U, V> Function<T, V> andThen(Function<T, U> first, Function<U, V> second) {
        return (T t) -> second.apply(first.apply(t));
    }

//    second.compose(first)
    static  <T, U, V> Function<T, V> compose(Function<U, V> first, Function<T, U> second) {
        return (T t) -> first.apply(second.apply(t));
    }


    public static void main(String[] args) {

        Function<String, Integer> parse = Integer::parseInt;

        Function<Integer, Integer> doubleIt =
                x -> x * 2;

        Function<String, Integer> pipeline =
                andThen(parse, doubleIt);

        System.out.println(pipeline.apply("10"));

    }
}
