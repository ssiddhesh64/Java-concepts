import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPoolChallenge {

    private static final AtomicInteger rejectedCount = new AtomicInteger(0);
    private static final String threadNamePrefix = "custom-worker-";

    /**
     * Requirement 1:
     * Implement a custom ThreadFactory that:
     * 1. Names threads in the format: "custom-worker-X" (where X is a unique sequential integer starting from 1).
     * 2. Sets the threads to be daemon threads.
     */
    public static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        
        @Override
        public Thread newThread(Runnable r) {
            // TODO: Implement custom Thread creation
            Thread newThread = new Thread(r, threadNamePrefix + threadNumber.getAndIncrement());
            newThread.setDaemon(true);
            return newThread;
        }
    }

    /**
     * Requirement 2:
     * Implement a custom RejectedExecutionHandler that acts as a Backpressure / Block-on-Rejection policy:
     * 
     * 1. When a task is rejected because the queue and pool are full, instead of discarding the task or throwing 
     *    an exception, the submitting thread should BLOCK until the task can be put back into the queue.
     * 2. Increment the `rejectedCount` counter.
     * 3. Hint: Use `executor.getQueue().put(r)` (which is a blocking call) to insert the task.
     */
    public static class BlockOnRejectionPolicy implements RejectedExecutionHandler {
        
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // TODO: Implement block-on-rejection backpressure policy
            if(executor.isShutdown()) {
                throw new RejectedExecutionException("Executor is shutdown");
            }

            rejectedCount.incrementAndGet();
            try {
                executor.getQueue().put(r);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Requirement 3:
     * Create and return a custom ThreadPoolExecutor configured with the following specifications:
     * 
     * 1. Core pool size: 2
     * 2. Maximum pool size: 4
     * 3. Keep-alive time: 1 second
     * 4. Queue: ArrayBlockingQueue with a capacity of 3
     * 5. Thread Factory: The custom CustomThreadFactory implemented above
     * 6. Rejection Handler: The custom BlockOnRejectionPolicy implemented above
     */
    public static ThreadPoolExecutor createCustomExecutor() {
        // TODO: Instantiate and return the configured ThreadPoolExecutor
        CustomThreadFactory threadFactory = new CustomThreadFactory();
        BlockOnRejectionPolicy rejectionPolicy = new BlockOnRejectionPolicy();
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(3);
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.SECONDS, queue, threadFactory, rejectionPolicy);
        return executor;
    }

    // Helper for delay
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Test Harness
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Starting Custom ThreadPoolExecutor Tests ===");

        ThreadPoolExecutor executor = createCustomExecutor();
        if (executor == null) {
            System.err.println("Executor is null. Implement the methods first.");
            return;
        }

        try {
            // Test 1: Verify Thread Factory (Names and Daemon status)
            System.out.println("\n--- Test 1: Thread Factory Validation ---");
            CompletableFuture<String> threadNameFuture = new CompletableFuture<>();
            CompletableFuture<Boolean> isDaemonFuture = new CompletableFuture<>();

            executor.submit(() -> {
                threadNameFuture.complete(Thread.currentThread().getName());
                isDaemonFuture.complete(Thread.currentThread().isDaemon());
            });

            String name = threadNameFuture.get(1, TimeUnit.SECONDS);
            boolean isDaemon = isDaemonFuture.get(1, TimeUnit.SECONDS);

            System.out.println("Worker thread name: " + name);
            System.out.println("Worker thread is daemon: " + isDaemon);

            if (name.startsWith("custom-worker-") && isDaemon) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE: Thread name must start with 'custom-worker-' and be a daemon");
            }

            // Test 2: Scaling and Backpressure (Submit 8 tasks)
            // Core: 2, Queue: 3 (max capacity before scaling is 5). Max: 4 (max capacity before rejection is 7).
            // Submitting 8 tasks will trigger the rejection policy, forcing the main thread to block.
            System.out.println("\n--- Test 2: Scaling & Rejection Backpressure Validation ---");
            rejectedCount.set(0);

            long start = System.currentTimeMillis();
            List<Future<?>> futures = new ArrayList<>();

            for (int i = 1; i <= 8; i++) {
                final int taskId = i;
                System.out.println("Submitting Task " + taskId + " (Active Threads: " + executor.getActiveCount() + ", Queue Size: " + executor.getQueue().size() + ")");
                
                futures.add(executor.submit(() -> {
                    sleep(200); // Simulate task work
                    return null;
                }));
            }

            long submissionDuration = System.currentTimeMillis() - start;
            System.out.println("All 8 tasks submitted in " + submissionDuration + " ms");
            System.out.println("Total rejected/throttled count: " + rejectedCount.get());

            // Wait for all tasks to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception ignored) {}
            }

            // Validations
            // With 200ms task duration:
            // The first 7 tasks are accepted immediately (2 core + 3 queue + 2 max).
            // The 8th task triggers the rejection handler because all 7 capacity slots are full.
            // The rejection handler blocks on queue.put(), so the main thread must wait.
            // Therefore, submissionDuration MUST be at least ~200ms, indicating the main thread was throttled.
            // And rejectedCount must be at least 1.
            boolean wasThrottled = submissionDuration >= 150;
            boolean hadRejections = rejectedCount.get() >= 1;

            if (wasThrottled && hadRejections) {
                System.out.println("SUCCESS: Backpressure verified!");
            } else {
                System.err.println("FAILURE:");
                System.err.println("  Main thread was throttled (>= 150ms): " + wasThrottled + " (" + submissionDuration + "ms)");
                System.err.println("  Rejection count >= 1: " + hadRejections + " (got " + rejectedCount.get() + ")");
            }

        } catch (Exception e) {
            System.err.println("Test failed with exception:");
            e.printStackTrace();
        } finally {
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
        }
    }
}
