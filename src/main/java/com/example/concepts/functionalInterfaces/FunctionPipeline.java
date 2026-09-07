package com.example.concepts.functionalInterfaces;

import java.util.function.Function;

public final class FunctionPipeline<T, R> {

    private final Function<T, R> function;

    private FunctionPipeline(Function<T, R> function) {
        this.function = function;
    }

    public static <T, R> FunctionPipeline<T, R> of(Function<T, R> function) {
        return new FunctionPipeline<>(function);
    }

    public R apply(T input) {
        return function.apply(input);
    }

    public <V> FunctionPipeline<T, V> andThen(Function<R, V> next) {
        return FunctionPipeline.of((T t) -> next.apply(this.apply(t)));
//        return FunctionPipeline.of(this.function.andThen(next));
    }

    public <V> FunctionPipeline<V, R> compose(Function<V, T> previous) {
        return FunctionPipeline.of((V v) -> this.apply(previous.apply(v)));
    }

    public static void main(String[] args) {
        FunctionPipeline<String, Integer> pipeline =
                FunctionPipeline.<String, Integer>of(Integer::parseInt)
                        .andThen(x -> x * 2)
                        .andThen(x -> x + 10);

        System.out.println(pipeline.apply("5"));
    }
}
