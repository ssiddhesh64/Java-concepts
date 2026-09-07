package com.example.concepts.functionalInterfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

//enum OrderStatus {
//    COMPLETED, PENDING, CANCELLED;
//}

//record Order(int id, String customer, double amount, OrderStatus status){}

public class Apply {

    static <T> List<T> filter(List<T> values, Predicate<T> pred) {
        List<T> res = new ArrayList<>();
        for(T val : values) {
            if(pred.test(val)) res.add(val);
        }
        return res;
    }

    static Consumer<List<?>> printList = (list) -> {
        System.out.println();
        list.forEach(v -> System.out.print(v + ", "));
        System.out.println();
    };

    public static void main(String[] args) {

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        printList.accept(numbers);
//        printList.accept(strings);

        System.out.println("===================================================================================");

        List<Order> orders = List.of(
                new Order(1, "Alice", 5000, OrderStatus.COMPLETED),
                new Order(2, "Bob", 12000, OrderStatus.PENDING),
                new Order(3, "Charlie", 3000, OrderStatus.COMPLETED),
                new Order(4, "David", 20000, OrderStatus.COMPLETED),
                new Order(5, "Eva", 8000, OrderStatus.CANCELLED)
        );

        processOrders(
                orders,
                order -> order.status().equals(OrderStatus.COMPLETED),
                order -> "Order #" + order.id()
                        + " - " + order.customer()
                        + " - ₹" + order.amount(),
                message -> System.out.println("LOG: " + message)
        );

        System.out.println("===================================================================================");

        Function<Order, Double> getAmount = order -> order.amount();
        Function<Double, Double> applyDiscount = amount -> 0.90 * amount;
        Function<Order, Double> finalAmount = getAmount.andThen(applyDiscount);
        Function<Order, String> formatAmount = amount -> " final amount " + amount;

        Function<Order, String> orderSummary =
                order -> order.customer()
                        + "'s final amount: ₹"
                        + finalAmount.apply(order);

        orders.forEach(order -> System.out.println(orderSummary.apply(order)));

    }

    static void processOrders(
            List<Order> orders,
            Predicate<Order> filter,
            Function<Order, String> transformer,
            Consumer<String> consumer) {

        for (Order order : orders) {
            if (filter.test(order)) {
                consumer.accept(transformer.apply(order));
            }
        }
    }
}
