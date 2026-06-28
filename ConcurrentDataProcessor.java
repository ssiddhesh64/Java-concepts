import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Before refactor
// public class ConcurrentDataProcessor {
//     public List data = new ArrayList();

//     public synchronized void addData(Object o) {
//         data.add(o);
//     }

//     public void processAll() {
//         for (int i = 0; i < data.size(); i++) {
//             final Object item = data.get(i);
//             Thread t = new Thread(new Runnable() {
//                 public void run() {
//                     // Simulate long running I/O operation
//                     try {
//                         Thread.sleep(1000);
//                     } catch (InterruptedException e) {
//                         e.printStackTrace();
//                     }
//                     System.out.println("Processed: " + item.toString());
//                 }
//             });
//             t.start();
//         }
//     }
// }

class StorageProcessor<T> {

    private final List<T> data = Collections.synchronizedList(new ArrayList<>());

    public void addData(T item) {
        data.add(item);
    }

    public List<T> snapshot() {
        synchronized (data) {
            return new ArrayList<>(data);
        }
    }
}

public class ConcurrentDataProcessor<T> {

    private final StorageProcessor<T> storageProcessor;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ConcurrentDataProcessor(
            StorageProcessor<T> storageProcessor) {
        this.storageProcessor = storageProcessor;
    }

    public CompletableFuture<Void> processAll() {

        List<T> snapshot = storageProcessor.snapshot();

        CompletableFuture<?>[] cfs = snapshot.stream()
                .map(item -> CompletableFuture.runAsync(
                        () -> processItem(item),
                        executorService))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(cfs);
    }

    private void processItem(T item) {
        try {
            Thread.sleep(1000);
            System.out.println("Processed: " + item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // throw new RuntimeException("Thread interrupted");
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

class ConcurrentDataProcessor2 {
    private final List<String> dataList = Collections.synchronizedList(new ArrayList<>());

    // Dedicated executor for I/O / heavy operations
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2);

    public CompletableFuture<Void> processData(List<String> rawData) {

        if (rawData == null)
            return CompletableFuture.completedFuture(null);

        List<String> snapshot;
        synchronized (rawData) {
            snapshot = new ArrayList<>(rawData);
        }

        List<CompletableFuture<Void>> futures = snapshot.stream()
                .map(data -> CompletableFuture.runAsync(
                        () -> {
                            String processed = heavyTransformation(data);
                            dataList.add(processed);
                        }, executor).exceptionally(ex -> {
                            // throw new RuntimeException("Unable to process");
                            return null;
                        }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // If we require results (CompletableFuture<List<String>> )
        // return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        // .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());

        // Manual thread creation in a loop (Anti-pattern!)
        // for (String data : rawData) {
        // new Thread(() -> {
        // try {
        // String processed = heavyTransformation(data);

        // dataList.add(processed);

        // // Direct printing/logging
        // System.out.println("Processed: " + processed);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }).start();
    }

    private String heavyTransformation(String input) {
        // Simulates heavy API call or DB calculation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return input.toUpperCase();
    }

    public void shutdown() {
        executor.shutdown();
    }

    public List<String> getDataList() {
        return dataList;
    }
}
