/**
 * CONCEPT TAUGHT: Scatter-Gather Concurrency Pattern
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates triggering multiple parallel tasks and waiting for them with a timeout.
 * 
 * KEY LESSONS:
 * - Trigger tasks in parallel (scatter), await their completion with timeout (gather), and merge results.
 * - Ensures slow external APIs do not block the thread pool indefinitely.
 */
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

record PriceDetail(String vendorId, double price) {}

public class ScatterGatherChallenge {

    // Dummy service simulating different vendor conditions
    public static CompletableFuture<PriceDetail> fetchPrice(String productId, String vendorId) {
        return CompletableFuture.supplyAsync(() -> {
            if ("slow_vendor".equals(vendorId)) {
                sleep(1500); // Exceeds individual timeout of 800ms
            } else if ("error_vendor".equals(vendorId)) {
                throw new RuntimeException("Vendor database offline");
            } else {
                sleep(200); // Fast vendor response
            }
            return new PriceDetail(vendorId, 99.99);
        });
    }

    /**
     * Implement this method to:
     * 
     * 1. Query the price for the productId from all vendors in the list in parallel.
     * 2. Apply an individual timeout of 800ms to each vendor query.
     * 3. Handle errors and timeouts for each vendor query individually, making sure a failure or timeout 
     *    in one vendor does not fail the other queries or the entire pipeline.
     * 4. Aggregate all successful PriceDetail results into a single CompletableFuture<List<PriceDetail>>.
     *    The resulting list must only contain non-null PriceDetails of successful queries.
     *    If all vendors fail/timeout, it should complete with an empty list.
     * 5. The implementation must be non-blocking (do not call `.get()` or `.join()` inside this method itself,
     *    except inside callbacks/functions executing *after* futures have finished).
     * 
     * @param productId the product ID to fetch prices for
     * @param vendors list of vendor IDs to query
     * @return a CompletableFuture containing the list of successfully retrieved PriceDetails
     */
    public static CompletableFuture<List<PriceDetail>> getBestPrices(String productId, List<String> vendors) {
        // TODO: Implement the resilient scatter-gather pattern

        List<CompletableFuture<PriceDetail>> futureList = vendors.stream()
        .map(vendorId -> fetchPrice(productId, vendorId)
                            .orTimeout(800, TimeUnit.MILLISECONDS)
                            .exceptionally(ex -> null))
        .collect(Collectors.toList());

        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
        .thenApply(v -> futureList.stream()
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
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
        System.out.println("=== Starting Resilient Scatter-Gather Tests ===");

        List<String> vendors = List.of("vendor_A", "slow_vendor", "vendor_B", "error_vendor", "vendor_C");

        long start = System.currentTimeMillis();
        try {
            List<PriceDetail> prices = getBestPrices("prod_100", vendors).get(3, TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - start;

            System.out.println("\n--- Results ---");
            System.out.println("Successful prices retrieved: " + prices);
            System.out.println("Total time taken: " + duration + " ms");

            // Validations
            boolean hasVendorA = prices.stream().anyMatch(p -> "vendor_A".equals(p.vendorId()));
            boolean hasVendorB = prices.stream().anyMatch(p -> "vendor_B".equals(p.vendorId()));
            boolean hasVendorC = prices.stream().anyMatch(p -> "vendor_C".equals(p.vendorId()));
            boolean hasSlow = prices.stream().anyMatch(p -> "slow_vendor".equals(p.vendorId()));
            boolean hasError = prices.stream().anyMatch(p -> "error_vendor".equals(p.vendorId()));

            if (hasVendorA && hasVendorB && hasVendorC && !hasSlow && !hasError) {
                System.out.println("\nSUCCESS: Resilient Scatter-Gather completed successfully!");
            } else {
                System.err.println("\nFAILURE: Returned list did not match expected criteria.");
                System.err.println("Expected: Only vendor_A, vendor_B, and vendor_C.");
                System.err.println("Got: " + prices);
            }

            // Verify parallelism (should take around 800ms-1000ms, not 1500ms+ since slow_vendor timed out)
            if (duration > 1200) {
                System.err.println("WARNING: Total duration was too long (" + duration + "ms). Are the tasks running in parallel?");
            }

        } catch (Exception e) {
            System.err.println("Test Failed with Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
