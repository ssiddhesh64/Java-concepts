import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MemoizerCache<K, V> {
    // private final Map<K, V> cache = new ConcurrentHashMap<>();
    private final Map<K, CompletableFuture<V>> cache = new ConcurrentHashMap<>();
    private final Executor executor;

    MemoizerCache(Executor executor) {
        this.executor = executor;
    }

    // High contention / Cache Stampede under load!
    public V get(K key, Function<K, V> computeFunction) {
        // V value = cache.get(key);
        CompletableFuture<V> future = cache
                .computeIfAbsent(key, k -> CompletableFuture.supplyAsync(
                        () -> computeFunction.apply(k),
                        executor));

        try {
            return future.join();
        } catch (CompletionException e) {
            cache.remove(key, future);
            throw e;
        }
        // return future;
        // if (value == null) {
        // // Multiple threads can reach here concurrently and trigger computeFunction!

        // value = computeFunction.apply(key);
        // cache.put(key, value);
        // }
        // return value;
    }
}
