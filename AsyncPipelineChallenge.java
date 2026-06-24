import java.util.concurrent.*;

record User(String userId, String email, double balance) {}
record Inventory(String productId, boolean isAvailable) {}
record PaymentResult(String transactionId, boolean success, String errorMessage) {}

public class AsyncPipelineChallenge {

    // Dummy service methods simulating network delay
    public static CompletableFuture<User> fetchUser(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(300); // Simulate network latency
            if ("invalid_user".equals(userId)) {
                throw new IllegalArgumentException("User not found");
            }
            return new User(userId, "user@example.com", 100.0);
        });
    }

    public static CompletableFuture<Inventory> checkInventory(String productId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(200); // Simulate network latency
            if ("out_of_stock".equals(productId)) {
                return new Inventory(productId, false);
            }
            return new Inventory(productId, true);
        });
    }

    public static CompletableFuture<PaymentResult> processPayment(User user, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(400); // Simulate processing latency
            if (user.balance() < amount) {
                return new PaymentResult("TX-ERR", false, "Insufficient balance");
            }
            return new PaymentResult("TX-OK-123", true, "");
        });
    }

    public static CompletableFuture<Void> sendNotification(User user, PaymentResult result) {
        return CompletableFuture.runAsync(() -> {
            sleep(150); // Simulate notification delay
            if ("TX-ERR".equals(result.transactionId())) {
                throw new RuntimeException("Notification failed: Transaction invalid");
            }
            if ("trigger_notification_error".equals(user.userId())) {
                throw new RuntimeException("Email server down");
            }
            System.out.println("[SERVICE] Notification sent successfully to " + user.email());
        });
    }

    /**
     * Implement this method to build a robust async order processing pipeline:
     * 
     * 1. Fetch the user profile and check product inventory in PARALLEL.
     * 2. If the product is out of stock (isAvailable is false), immediately fail the pipeline 
     *    with an IllegalStateException("Product out of stock").
     * 3. Once both tasks complete successfully, process the payment for the given amount.
     * 4. If the payment fails (success is false), fail the pipeline with an IllegalStateException
     *    containing the payment error message.
     * 5. If the payment is successful, send a notification to the user.
     * 6. Resilience: If the notification service fails (throws an exception), the overall pipeline 
     *    should STILL complete successfully with the PaymentResult (graceful degradation).
     * 7. Timeout: The entire process must complete within 2 seconds. If it takes longer, 
     *    it must fail with a TimeoutException.
     * 
     * @return a CompletableFuture containing the PaymentResult on success
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    public static CompletableFuture<PaymentResult> processOrder(String userId, String productId, double amount) {
        // TODO: Implement this pipeline using CompletableFuture chaining methods
        CompletableFuture<User> userFuture = fetchUser(userId);
        CompletableFuture<Inventory> inventoryFuture = checkInventory(productId);

        return userFuture
        .thenCombine(
            inventoryFuture, (user, inventory) -> {
                if(!inventory.isAvailable()) {
                    throw new IllegalStateException("Product out of stock");
                }
                return user;
            }
        )
        .thenCompose(user -> {
            return processPayment(user, amount)
                .thenCompose(payment -> {

                    if(!payment.success()) {
                        return CompletableFuture.failedFuture(new IllegalStateException(payment.errorMessage()));
                    }

                    return sendNotification(user, payment)
                            .exceptionally(ex -> {
                                System.err.println("Notification Failed: " + ex.getMessage());
                                return null;
                            })
                            .thenApply(v -> payment);
                    }
                );
            }
        )
        .orTimeout(2, TimeUnit.SECONDS);
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
        System.out.println("=== Starting Async Pipeline Tests ===");

        // Test 1: Successful Order Flow
        System.out.println("\n--- Test 1: Successful Order Flow ---");
        try {
            PaymentResult res = processOrder("user_01", "product_01", 50.0).get(3, TimeUnit.SECONDS);
            System.out.println("Result: " + res);
        } catch (Exception e) {
            System.err.println("Test 1 Failed: " + e.getCause());
        }

        // Test 2: Inventory Out of Stock
        System.out.println("\n--- Test 2: Inventory Out of Stock ---");
        try {
            processOrder("user_01", "out_of_stock", 50.0).get(3, TimeUnit.SECONDS);
            System.err.println("Test 2 Failed: Expected exception but succeeded");
        } catch (Exception e) {
            System.out.println("Caught Expected Exception: " + e.getCause());
        }

        // Test 3: User Not Found
        System.out.println("\n--- Test 3: User Not Found ---");
        try {
            processOrder("invalid_user", "product_01", 50.0).get(3, TimeUnit.SECONDS);
            System.err.println("Test 3 Failed: Expected exception but succeeded");
        } catch (Exception e) {
            System.out.println("Caught Expected Exception: " + e.getCause());
        }

        // Test 4: Insufficient Balance
        System.out.println("\n--- Test 4: Insufficient Balance ---");
        try {
            processOrder("user_01", "product_01", 150.0).get(3, TimeUnit.SECONDS);
            System.err.println("Test 4 Failed: Expected exception but succeeded");
        } catch (Exception e) {
            System.out.println("Caught Expected Exception: " + e.getCause());
        }

        // Test 5: Notification Service Fails (Graceful Degradation)
        System.out.println("\n--- Test 5: Notification Service Fails (Graceful Degradation) ---");
        try {
            PaymentResult res = processOrder("trigger_notification_error", "product_01", 50.0).get(3, TimeUnit.SECONDS);
            System.out.println("Result: " + res + " (Should succeed even if notification failed)");
        } catch (Exception e) {
            System.err.println("Test 5 Failed: " + e.getCause());
        }
    }
}
