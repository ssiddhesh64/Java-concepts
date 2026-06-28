import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NestedTaskProcessor {
    // Dedicated fixed thread pool for data processing
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    // private final ExecutorService subPool = Executors.newFixedThreadPool(4);

    // Will cause deadlock
    public void processAll(List<String> items) throws Exception {
        List<Future<Void>> parentFutures = new ArrayList<>();

        for (String item : items) {
            parentFutures.add(pool.submit(() -> {
                // Subtask submitted to the SAME pool
                Future<String> subTaskFuture = pool.submit(() -> subProcess(item));

                // Blocks the parent thread waiting for the subtask to execute
                String result = subTaskFuture.get();

                System.out.println("Finished: " + result);
                return null;
            }));
        }

        // Wait for all parent tasks to complete
        for (Future<Void> f : parentFutures) {
            f.get();
        }

        shutdown();
    }

    // Better way
    public CompletableFuture<Void> processAllAsync(List<String> items) {
        List<CompletableFuture<Void>> futures = items.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> subProcess(item), pool)
                        .thenAccept(result -> System.out.println("Finished: " + result)))
                .toList();

        // No threads are blocked waiting. Everything is event-driven.
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void shutdown() {
        pool.shutdown();
        // subPool.shutdown();
    }

    private String subProcess(String item) {
        return item.toUpperCase();
    }

    public static void main(String[] args) {
        NestedTaskProcessor processor = new NestedTaskProcessor();
        try {
            processor.processAllAsync(List.of("a", "b", "c", "a", "b", "c"));
            // processor.processAll(List.of("a", "b", "c"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
