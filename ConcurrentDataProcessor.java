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

    private final List<T> data =
            Collections.synchronizedList(new ArrayList<>());

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

    private final ExecutorService executorService =
            Executors.newFixedThreadPool(4);

    public ConcurrentDataProcessor(
            StorageProcessor<T> storageProcessor) {
        this.storageProcessor = storageProcessor;
    }

    public CompletableFuture<Void> processAll() {

        List<T> snapshot =
                storageProcessor.snapshot();

        CompletableFuture<?>[] cfs = snapshot.stream()
        .map(item -> 
            CompletableFuture.runAsync(
                () -> processItem(item),
                executorService)
        ).toArray(CompletableFuture[]::new);

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
