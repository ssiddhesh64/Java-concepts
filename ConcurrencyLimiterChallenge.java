/**
 * CONCEPT TAUGHT: Semaphore-based Rate/Concurrency Limiting
 * 
 * WHY THIS WAS WRITTEN:
 * - A challenge to restrict concurrent execution of a slow method using a Semaphore.
 * 
 * KEY LESSONS:
 * - Semaphores are ideal for restricting the maximum number of concurrent threads entering a block of code.
 * - Always call semaphore.release() in a finally block to prevent resource leaks on exceptions.
 */
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ConcurrencyLimiterChallenge {

    // Atomic tracker to verify the concurrency limit is respected at runtime
    private static final AtomicInteger activeThreads = new AtomicInteger(0);
    private static final AtomicInteger maxObservedConcurrency = new AtomicInteger(0);

    /**
     * Helper to create a task that simulates latency and tracks concurrency.
     */
    public static Supplier<CompletableFuture<String>> createTask(String name, long delayMs, boolean shouldFail) {
        return () -> CompletableFuture.supplyAsync(() -> {
            int currentActive = activeThreads.incrementAndGet();
            // Track the maximum concurrency observed
            maxObservedConcurrency.updateAndGet(max -> Math.max(max, currentActive));

            try {
                sleep(delayMs);
                if (shouldFail) {
                    throw new RuntimeException("Task " + name + " failed");
                }
                return "Result_" + name;
            } finally {
                activeThreads.decrementAndGet();
            }
        });
    }

    /**
     * Implement this method to run a list of tasks with a strict limit on concurrency:
     * 
     * 1. Start at most `maxConcurrent` tasks initially.
     * 2. As soon as any running task completes successfully, start the next pending task.
     * 3. Collect the results of all tasks and return them in a list that preserves the original index order.
     * 4. If any task fails, the returned future should immediately complete exceptionally with that exception.
     *    Further pending tasks should not be started.
     * 5. The entire implementation must be non-blocking (do not call `.get()`, `.join()`, or block a thread).
     * 6. If the input list is null or empty, return a completed future with an empty list.
     */
    public static <T> CompletableFuture<List<T>> runWithConcurrencyLimit(
            List<Supplier<CompletableFuture<T>>> tasks, int maxConcurrent) {
        // TODO: Implement the non-blocking concurrent task limiter
        if(tasks == null || tasks.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        CompletableFuture<List<T>> ans = new CompletableFuture<>();

        int initialTasks = Math.min(maxConcurrent, tasks.size());
        AtomicInteger nextId = new AtomicInteger(initialTasks);
        AtomicInteger completed = new AtomicInteger(0);
        AtomicBoolean failed = new AtomicBoolean(false);

        List<T> ansList = new ArrayList<>(Collections.nCopies(tasks.size(), null));

        class Scheduler {

            void startTask(int taskIndex) {
                if(failed.get()) {
                    return;
                }

                tasks.get(taskIndex)
                .get()
                .whenComplete((res, ex) -> {
                    if(ex != null) {
                        failed.compareAndSet(false, true);
                        ans.completeExceptionally(ex);
                        return;
                    }

                    ansList.set(taskIndex, res);
                    if(completed.incrementAndGet() == tasks.size()) {
                        ans.complete(ansList);
                    }

                    int next = nextId.getAndIncrement();
                    if(next < tasks.size()) {
                        startTask(next);
                    }
                });

            }
        }

        Scheduler scheduler = new Scheduler();

        for(int i = 0; i < initialTasks; i++) {
            scheduler.startTask(i);
        }
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
        System.out.println("=== Starting Concurrency Limiter Tests ===");

        // Test 1: Concurrency Limit of 2 with 5 tasks
        System.out.println("\n--- Test 1: Concurrency Limit of 2 ---");
        maxObservedConcurrency.set(0);
        activeThreads.set(0);

        List<Supplier<CompletableFuture<String>>> tasks = List.of(
            createTask("A", 200, false),
            createTask("B", 300, false),
            createTask("C", 150, false),
            createTask("D", 250, false),
            createTask("E", 100, false)
        );

        long start = System.currentTimeMillis();
        try {
            List<String> results = runWithConcurrencyLimit(tasks, 2).get(3, TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - start;

            System.out.println("Results: " + results);
            System.out.println("Max observed concurrent tasks running: " + maxObservedConcurrency.get());
            System.out.println("Total duration: " + duration + " ms");

            // Assertions
            boolean orderPreserved = List.of("Result_A", "Result_B", "Result_C", "Result_D", "Result_E").equals(results);
            boolean limitRespected = maxObservedConcurrency.get() <= 2;

            if (orderPreserved && limitRespected) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE");
                System.err.println("  Order preserved: " + orderPreserved);
                System.err.println("  Concurrency limit respected: " + limitRespected);
            }

        } catch (Exception e) {
            System.err.println("Test 1 Failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 2: Fail Fast Behavior
        System.out.println("\n--- Test 2: Fail Fast Behavior ---");
        maxObservedConcurrency.set(0);
        activeThreads.set(0);

        List<Supplier<CompletableFuture<String>>> failingTasks = List.of(
            createTask("F", 100, false),
            createTask("G", 150, true), // Task G will fail
            createTask("H", 200, false)
        );

        try {
            runWithConcurrencyLimit(failingTasks, 2).get(3, TimeUnit.SECONDS);
            System.err.println("Test 2 Failed: Expected exception but succeeded");
        } catch (Exception e) {
            System.out.println("Caught Expected Exception: " + e.getCause().getMessage());
            System.out.println("SUCCESS");
        }
    }
}
