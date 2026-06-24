import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RacingFuturesChallenge {

    // Dummy helper simulating latency and success/failure
    public static CompletableFuture<String> callService(String name, long delayMs, boolean shouldFail) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(delayMs);
            if (shouldFail) {
                throw new RuntimeException("Service " + name + " encountered an error");
            }
            return "Result from " + name;
        });
    }

    /**
     * Implement this method to:
     * 
     * Return a CompletableFuture that completes with the value of the FIRST future in the list
     * to complete successfully.
     * 
     * Requirements:
     * 1. If any future completes successfully, the returned future should immediately complete with that result.
     * 2. If a future fails, it should be ignored unless ALL futures in the list fail.
     * 3. If ALL futures in the list fail (complete exceptionally), then the returned future should
     *    complete exceptionally with a RuntimeException("All services failed").
     * 4. The implementation must be non-blocking (do not call `.get()` or `.join()` inside this method).
     * 5. If the input list is null or empty, return a failed future with an IllegalArgumentException.
     * 
     * Note: Do NOT use CompletableFuture.anyOf() directly, as anyOf completes exceptionally if the 
     * first completing future fails, whereas we want to wait for the first SUCCESSFUL future.
     */
    public static <T> CompletableFuture<T> firstSuccessful(List<CompletableFuture<T>> futures) {
        // TODO: Implement this racing/hedging pattern

        if(futures == null || futures.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("futures is null or empty"));
        }
        AtomicInteger count = new AtomicInteger(0);
        CompletableFuture<T> ans = new CompletableFuture<>();
        futures.stream()
        .forEach(f -> {
            f.whenComplete((res, ex) -> {
                if(ex != null) {
                    if(count.incrementAndGet() == futures.size()) {
                        ans.completeExceptionally(new RuntimeException("All services failed"));
                    }
                } else {
                    ans.complete(res);
                }
            });
        });

        return ans;
    }

    // Helper method for delay
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Test Harness
    public static void main(String[] args) {
        System.out.println("=== Starting Racing Futures Tests ===");

        // Test 1: Fastest completes successfully
        System.out.println("\n--- Test 1: Fastest service succeeds ---");
        CompletableFuture<String> f1 = callService("ServiceA", 200, false);
        CompletableFuture<String> f2 = callService("ServiceB", 600, false);
        try {
            String res = firstSuccessful(List.of(f1, f2)).get(2, TimeUnit.SECONDS);
            System.out.println("Returned: " + res);
            if ("Result from ServiceA".equals(res)) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE: Expected ServiceA but got " + res);
            }
        } catch (Exception e) {
            System.err.println("Test 1 Failed: " + e.getMessage());
        }

        // Test 2: Fastest fails, slower succeeds
        System.out.println("\n--- Test 2: Fastest fails, slower succeeds ---");
        CompletableFuture<String> f3 = callService("ServiceC (Fast but Fail)", 100, true);
        CompletableFuture<String> f4 = callService("ServiceD (Slower but OK)", 400, false);
        try {
            String res = firstSuccessful(List.of(f3, f4)).get(2, TimeUnit.SECONDS);
            System.out.println("Returned: " + res);
            if ("Result from ServiceD (Slower but OK)".equals(res)) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE: Expected ServiceD but got " + res);
            }
        } catch (Exception e) {
            System.err.println("Test 2 Failed: " + e.getMessage());
        }

        // Test 3: All services fail
        System.out.println("\n--- Test 3: All services fail ---");
        CompletableFuture<String> f5 = callService("ServiceE", 100, true);
        CompletableFuture<String> f6 = callService("ServiceF", 200, true);
        try {
            firstSuccessful(List.of(f5, f6)).get(2, TimeUnit.SECONDS);
            System.err.println("Test 3 Failed: Expected completion exceptionally but succeeded");
        } catch (Exception e) {
            System.out.println("Caught Expected Exception: " + e.getCause().getMessage());
            if (e.getCause().getMessage().contains("All services failed")) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE: Unexpected exception message: " + e.getCause().getMessage());
            }
        }
    }
}
