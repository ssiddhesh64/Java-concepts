package com.example.concepts.streams;

/**
 * CONCEPT TAUGHT: Stream groupingBy Collector
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates how to group stream elements by a property into a Map.
 * 
 * KEY LESSONS:
 * - Collectors.groupingBy() splits a stream into buckets.
 * - Downstream collectors can perform aggregations (like counting or averaging) within each bucket.
 */
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupBy {

    public static Map<String, Double> getAverageAmountByCategory(List<Transaction> transactions) {

        if(transactions == null || transactions.isEmpty()) {
            return Map.of();
        }

        Map<String, Double> res = transactions.stream()
        .collect(Collectors.groupingBy(Transaction::category, Collectors.averagingDouble(Transaction::amount)));

        return res;
    } 
    
    public static void main(String[] args) {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("Amazon", "Electronics", 1000),
                new Transaction("Flipkart", "Electronics", 2000),
                new Transaction("Myntra", "Fashion", 3000),
                new Transaction("Ajio", "Fashion", 4000),
                new Transaction("Amazon", "Fashion", 5000));

                
    }

    // Nested helper class to prevent namespace conflicts
    static record Transaction(String merchant, String category, double amount) {}

}