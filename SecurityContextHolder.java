/**
 * CONCEPT TAUGHT: ThreadLocal Storage Pattern
 * 
 * WHY THIS WAS WRITTEN:
 * - Shows how to associate user contexts/security contexts with the current executing thread.
 * 
 * KEY LESSONS:
 * - ThreadLocal isolates data per-thread.
 * - Always call .remove() in a finally block to avoid memory leaks in thread pools.
 */
import java.util.concurrent.*;

public class SecurityContextHolder {
    private static final ThreadLocal<UserContext> context = new ThreadLocal<>();

    public static void setContext(UserContext user) {
        context.set(user);
    }

    public static UserContext getContext() {
        return context.get();
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        // Request 1 (User Admin)
        threadPool.submit(() -> {
            try {
                setContext(new UserContext("Admin"));
                System.out.println("Thread 1: User is " + getContext().username);
                // Request processed...
            } finally {
                context.remove();
            }
        });

        // Sleep to let task 1 finish
        Thread.sleep(100);

        // Request 2 (Guest User - does not authenticate/set context)
        threadPool.submit(() -> {
            UserContext curUser = getContext();
            if (curUser != null) {
                System.out.println("Thread 2: User is " + curUser.username + " (ALERT: Security Leak!)");
            } else {
                System.out.println("Thread 2: User is Guest (Safe)");
            }
        });

        threadPool.shutdown();
    }
}

class UserContext {
    String username;
    UserContext(String username) { this.username = username; }
}
