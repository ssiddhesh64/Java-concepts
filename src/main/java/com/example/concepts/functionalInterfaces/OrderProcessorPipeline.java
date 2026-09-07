package com.example.concepts.functionalInterfaces;

import com.example.concepts.solid.ApplyExample;

import java.util.List;
import java.util.function.*;

record Order(int id, String customer, double amount, OrderStatus status) {}

enum OrderStatus {
    COMPLETED, PENDING, CANCELLED;
}

public class OrderProcessorPipeline {

    public static void main(String[] args) {

        final double discountPercent = 10.0;
        List<Order> orders = getOrders();
        Supplier<Double> discountPercentage = () -> discountPercent;
        Predicate<OrderStatus> isCompleted = (o) -> o.equals(OrderStatus.COMPLETED);
        BiFunction<Double, Double, Double> discount = (amount, discoutPercent) -> amount * (1 - discoutPercent / 100.0);
        Function<Order, Double> calculateFinalAmount = o -> discount.apply(o.amount(), discountPercentage.get());
        Function<Order, String> transformOrder = o -> o.customer() + " final amount " + calculateFinalAmount.apply(o);
        Consumer<String> sendOrder = (order) -> System.out.println("SEND: " + order);


        processOrders(orders, isCompleted, transformOrder, sendOrder);

    }

    static void processOrders(List<Order> orders, Predicate<OrderStatus> filterByStatus, Function<Order, String> transformOrder, Consumer<String> sendOrder) {

        orders.forEach(o -> {
            if(filterByStatus.test(o.status())) {
                sendOrder.accept(transformOrder.apply(o));
            }
        });
    }

    private static List<Order> getOrders() {
        return List.of(
                new Order(1, "Alice", 5000, OrderStatus.COMPLETED),
                new Order(2, "Bob", 12000, OrderStatus.PENDING),
                new Order(3, "Charlie", 3000, OrderStatus.COMPLETED),
                new Order(4, "David", 20000, OrderStatus.COMPLETED),
                new Order(5, "Eva", 8000, OrderStatus.CANCELLED)
        );
    }
}
