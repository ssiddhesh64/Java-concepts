package com.example.concepts.concurrency;

/**
 * CONCEPT TAUGHT: CompletableFuture Pipeline Assembly
 * 
 * WHY THIS WAS WRITTEN:
 * - Shows how to chain, combine, and handle errors in async pipelines.
 * 
 * KEY LESSONS:
 * - Use thenCombine() to combine the results of two independent futures.
 * - Use exceptionally() at the end of the pipeline to catch all intermediate errors.
 */

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.List;

public class FutureFetchAndCalculate {

    public static CompletableFuture<Double> fetchAndCalculate(
            Supplier<Double> source1,
            Supplier<Double> source2,
            BiFunction<Double, Double, Double> combiner) {
        CompletableFuture<Double> future1 = CompletableFuture.supplyAsync(source1);
        CompletableFuture<Double> future2 = CompletableFuture.supplyAsync(source2);

        return future1.thenCombine(future2, combiner)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        System.out.println("Computation Failed: " + ex.getMessage());
                        // Exception will be thrown automatically
                        // try {
                        // throw ex;
                        // } catch (Throwable ex1) {
                        // System.getLogger(FutureFetchAndCalculate.class.getName()).log(System.Logger.Level.ERROR,
                        // (String) null, ex1);
                        // }
                    }
                });
    }

    public static CompletableFuture<String> fetchUserEmail(String userId) {
        return CompletableFuture.supplyAsync(() -> "email");
    }

    public static CompletableFuture<Void> sendVerificationCode(String email) {
        return CompletableFuture.runAsync(() -> System.out.println("Sending verification code to: " + email));
    }

    public static CompletableFuture<String> fetchFromApi(String url) {
        return CompletableFuture.supplyAsync(() -> "Result from api: " + url);
    }

    public static CompletableFuture<String> fetchUserDataAndFormat(String userId) {

        return fetchUserEmail(userId)
                .thenCompose(email -> sendVerificationCode(email)
                        .thenApply(ignored -> "Verification code sent to " + email))
                .exceptionally(ex -> "Failed to process verification for user: " + userId);

    }

    public static CompletableFuture<List<String>> fetchAllData(List<String> uris) {
        CompletableFuture<List<String>> results = new CompletableFuture<>();
        AtomicInteger success = new AtomicInteger(0);
        List<String> ans = new ArrayList<>();
        uris.parallelStream()
                .forEach(url -> {
                    fetchFromApi(url)
                            .whenComplete((res, ex) -> {
                                if (ex != null) {
                                    throw new RuntimeException("Error while fetching url: " + url);
                                }
                                ans.add(res);
                                if (success.incrementAndGet() == uris.size()) {
                                    results.complete(ans);
                                }
                            });
                });

        return results;
    }

}