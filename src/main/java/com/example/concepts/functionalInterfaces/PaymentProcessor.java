package com.example.concepts.functionalInterfaces;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

record Payment(
        int id,
        double amount,
        String currency,
        String status
) {}

public class PaymentProcessor {

    public static void main(String[] args) {

        List<Payment> payments = List.of(
                new Payment(1, 100, "USD", "PENDING"),
                new Payment(2, 200, "USD", "COMPLETED"),
                new Payment(3, 150, "USD", "PENDING"),
                new Payment(4, 500, "EUR", "PENDING")
        );

        // Supplier: dynamically provides exchange rate
        Supplier<Double> usdToInrRate = () -> 85.0;

        // Predicate: determines which payments should be processed
        Predicate<Payment> pendingPayment =
                payment -> payment.status().equals("PENDING");

        // Function: converts the payment amount using the exchange rate
        Function<Payment, Double> convertToInr =
                payment -> payment.amount() * usdToInrRate.get();

        // BiFunction: applies processing fee
        BiFunction<Double, Double, Double> applyProcessingFee =
                (amount, feePercentage) ->
                        amount * (1 - feePercentage / 100);

        // Configurable processing fee
        double processingFee = 2.0;

        // Function: creates receipt
        Function<Payment, String> createReceipt =
                payment -> {
                    double convertedAmount = convertToInr.apply(payment);

                    double finalAmount = applyProcessingFee.apply(
                            convertedAmount,
                            processingFee
                    );

                    return "Payment #" + payment.id()
                            + " - " + payment.currency() + " "
                            + payment.amount()
                            + " -> ₹" + finalAmount;
                };

        // Consumer: sends the receipt
        Consumer<String> sendReceipt =
                receipt -> System.out.println("SEND: " + receipt);

        processPayments(
                payments,
                pendingPayment,
                createReceipt,
                sendReceipt
        );
    }

    static void processPayments(
            List<Payment> payments,
            Predicate<Payment> filter,
            Function<Payment, String> transformer,
            Consumer<String> consumer
    ) {

        for (Payment payment : payments) {

            if (filter.test(payment)) {
                String receipt = transformer.apply(payment);
                consumer.accept(receipt);
            }
        }
    }
}
