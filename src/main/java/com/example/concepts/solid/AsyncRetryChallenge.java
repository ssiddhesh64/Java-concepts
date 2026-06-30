package com.example.concepts.solid;

/**
 * CONCEPT TAUGHT: Non-blocking Asynchronous Retries
 * 
 * WHY THIS WAS WRITTEN:
 * - Shows how to implement a retry mechanism for async tasks without blocking threads using a ScheduledExecutorService.
 * 
 * KEY LESSONS:
 * - Do not use Thread.sleep() inside async tasks as it blocks carrier/pool threads.
 * - Use ScheduledExecutorService.schedule() to trigger delayed attempts asynchronously.
 * - Chain delayed attempts recursively via CompletableFuture pipelines.
 */
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class AsyncRetryChallenge {

    // Helper service to simulate flaky operations
    static class FlakyService {
        private final int failAttempts;
        private final AtomicInteger attempts = new AtomicInteger(0);

        public FlakyService(int failAttempts) {
            this.failAttempts = failAttempts;
        }

        public CompletableFuture<String> call() {
            return CompletableFuture.supplyAsync(() -> {
                int current = attempts.incrementAndGet();
                if (current <= failAttempts) {
                    throw new RuntimeException("Network timeout (attempt " + current + ")");
                }
                return "Success on attempt " + current;
            });
        }

        public int getAttempts() {
            return attempts.get();
        }
    }

    /**
     * Implement this method to build a non-blocking asynchronous retry mechanism:
     * 
     * 1. Execute the future provided by `taskSupplier`.
     * 2. If it succeeds, return the result.
     * 3. If it fails and `maxAttempts` has not been reached, wait for `delayMs` (using the provided `scheduler` 
     *    non-blockingly) and try again.
     * 4. If all attempts fail, complete the returned future exceptionally with the exception from the final attempt.
     * 5. The implementation must be non-blocking (do not use Thread.sleep() or block on futures).
     * 
     * @param taskSupplier supplier of the future task to run
     * @param maxAttempts maximum number of execution attempts
     * @param delayMs time to wait in milliseconds between attempts
     * @param scheduler scheduled executor to run delayed retries non-blockingly
     * @return a CompletableFuture representing the result of the task after retries
     */
    public static <T> CompletableFuture<T> retryAsync(
            Supplier<CompletableFuture<T>> taskSupplier, 
            int maxAttempts, 
            long delayMs, 
            ScheduledExecutorService scheduler) {
        // TODO: Implement the non-blocking async retry logic
        CompletableFuture<T> ans = new CompletableFuture<>();
        attempt(taskSupplier, 1, maxAttempts, delayMs, scheduler, ans);
        return ans;
    }

    public static <T> void attempt(Supplier<CompletableFuture<T>> taskSupplier, int currentAttempt, int maxAttempts, long delayMs, ScheduledExecutorService scheduler, CompletableFuture<T> ans) {
        CompletableFuture<T> future;
        try {
            future = taskSupplier.get();
            if (future == null) {
                future = CompletableFuture.failedFuture(new NullPointerException("Supplier returned a null future"));
            }
        } catch (Throwable ex) {
            future = CompletableFuture.failedFuture(ex);
        }

        future.whenComplete((res, ex) -> {
            if(ex == null) {
                ans.complete(res);
                return;
            }
            if(currentAttempt < maxAttempts) {
                scheduler.schedule(() -> attempt(taskSupplier, currentAttempt + 1, maxAttempts, delayMs, scheduler, ans), delayMs, TimeUnit.MILLISECONDS);
            } else {
                ans.completeExceptionally(ex);
            }
        });
    }

    // Test Harness
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        System.out.println("=== Starting Async Retry Tests ===");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        try {
            // Test 1: Succeeds on 3rd attempt, maxAttempts = 3, delay = 100ms
            System.out.println("\n--- Test 1: Flaky service succeeding eventually ---");
            FlakyService service1 = new FlakyService(2); // Fails twice, succeeds on 3rd
            CompletableFuture<String> res1 = retryAsync(service1::call, 3, 100, scheduler);
            String result1 = res1.get(2, TimeUnit.SECONDS);
            System.out.println("Returned: " + result1);
            if ("Success on attempt 3".equals(result1) && service1.getAttempts() == 3) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE");
            }

            // Test 2: Fails completely (fails 3 times, maxAttempts = 2)
            System.out.println("\n--- Test 2: Flaky service failing completely ---");
            FlakyService service2 = new FlakyService(5);
            CompletableFuture<String> res2 = retryAsync(service2::call, 2, 100, scheduler);
            try {
                res2.get(2, TimeUnit.SECONDS);
                System.err.println("FAILURE: Expected exception but succeeded");
            } catch (Exception e) {
                System.out.println("Caught Expected Exception: " + e.getCause().getMessage());
                if (service2.getAttempts() == 2) {
                    System.out.println("SUCCESS");
                } else {
                    System.err.println("FAILURE: Expected exactly 2 attempts, but got " + service2.getAttempts());
                }
            }

            // Test 3: Instant success
            System.out.println("\n--- Test 3: Stable service succeeds instantly ---");
            FlakyService service3 = new FlakyService(0); // 0 failures
            CompletableFuture<String> res3 = retryAsync(service3::call, 3, 100, scheduler);
            String result3 = res3.get(2, TimeUnit.SECONDS);
            System.out.println("Returned: " + result3);
            if ("Success on attempt 1".equals(result3) && service3.getAttempts() == 1) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE");
            }

        } finally {
            scheduler.shutdown();
        }
    }

}