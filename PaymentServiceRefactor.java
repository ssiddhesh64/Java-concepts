/**
 * CONCEPT TAUGHT: Non-blocking Asynchronous Retries with CompletableFuture
 * 
 * WHY THIS WAS WRITTEN:
 * - Refactors a blocking retry payment loop into a stateless, non-blocking async pipeline.
 * 
 * KEY LESSONS:
 * - Do not mix synchronous loops with asynchronous scheduling.
 * - Do not put mutable state (like retry count) in service fields.
 * - ScheduledExecutorService is required for delayed execution.
 * - Chain retries recursively using handle() and thenCompose().
 */
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

interface NotificationService {
    void sendEmail(String email, String message);

}

class EmailNotificationService implements NotificationService {
    @Override
    public void sendEmail(String email, String message) {
        System.out.println("Sending email to: " + email);
        System.out.println("Message: " + message);
    }
}

interface PaymentGateway {
    void processPayment(Order order);
}

class StripeGateway implements PaymentGateway {
    @Override
    public void processPayment(Order order) {
        System.out.println("Connecting to Stripe...");
        if (order.amount() > 1000) {
            throw new RuntimeException("Gateway Timeout");
        }
        System.out.println("Payment successful of $" + order.amount());
    }
}

public class PaymentServiceRefactor {

    ScheduledExecutorService scheduler;
    PaymentGateway paymentGateway;
    NotificationService notificationService;

    PaymentServiceRefactor(ScheduledExecutorService executor, PaymentGateway paymentGateway,
            NotificationService notificationService) {
        this.scheduler = executor;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
    }

    public CompletableFuture<Boolean> processPaymentAsync(Order order) {
        try {
            validateOrder(order);
            return attemptPaymentAsync(order, 3);
        } catch (IllegalArgumentException ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    private CompletableFuture<Boolean> attemptPaymentAsync(Order order, int remainingRetries) {
        return CompletableFuture.supplyAsync(() -> {
            paymentGateway.processPayment(order);
            notificationService.sendEmail(order.email(), "Payment Successful");
            return true;
        }, scheduler).handle((sucess, ex) -> {
            if (ex == null) {
                return CompletableFuture.completedFuture(true);
            }

            if (remainingRetries <= 1) {
                System.out.println("Payment Failed. All retries exhausted.");
                return CompletableFuture.completedFuture(false);
            }

            System.out.println("Failed. Retrying in 1 second. Remaining retries: " + (remainingRetries - 1));

            CompletableFuture<Boolean> nextAttempt = new CompletableFuture<>();
            scheduler.schedule(() -> {
                attemptPaymentAsync(order, remainingRetries - 1).thenAccept(nextAttempt::complete);
            }, 1, TimeUnit.SECONDS);

            return nextAttempt;
        }).thenCompose(future -> future);
    }

    // public boolean processPayment(Order order) {

    // if (order != null) {
    // if (order.getAmount() > 0) {
    // int retries = 3;
    // while (retries > 0) {
    // try {
    // // Hardcoded gateway call (Tight coupling!)
    // System.out.println("Connecting to Stripe...");
    // if (order.getAmount() > 1000) {
    // throw new RuntimeException("Gateway Timeout");
    // }
    // System.out.println("Payment successful of $" + order.getAmount());

    // // Send email (Tight coupling!)
    // System.out.println("Email sent to " + order.getEmail());
    // return true;
    // } catch (Exception e) {
    // retries--;
    // System.out.println("Failed. Retrying in 1 second. Remaining: " + retries);
    // try {
    // Thread.sleep(1000); // Blocking thread inside loop!
    // } catch (InterruptedException ie) {
    // Thread.currentThread().interrupt();
    // }
    // }
    // }
    // return false;
    // } else {
    // throw new IllegalArgumentException("Amount must be positive");
    // }
    // } else {
    // throw new IllegalArgumentException("Order cannot be null");
    // }

    // validateOrder(order);
    // while (retries.get() > 0) {
    // try {
    // paymentGateway.processPayment(order);
    // retries.set(0);
    // notificationService.sendEmail(order.email(), "Payment successful");
    // return true;
    // } catch (Exception ex) {
    // retries.decrementAndGet();
    // System.out.println("Failed. Retrying in 1 second. Remaining: " + retries);
    // scheduler.schedule(() -> processPayment(order), 1, TimeUnit.SECONDS);
    // }
    // }

    // return false;
    // }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.amount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}

record Order(double amount, String email) {
}

// Order class for reference
// class Order {
// private double amount;
// private String email;

// public Order(double amount, String email) {
// this.amount = amount;
// this.email = email;
// }

// public double getAmount() {
// return amount;
// }

// public String getEmail() {
// return email;
// }
// }
