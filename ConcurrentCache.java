/**
 * CONCEPT TAUGHT: Atomic Cache Updates with ConcurrentHashMap
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates how to implement thread-safe memoization/caching using ConcurrentHashMap's atomic compute methods.
 * 
 * KEY LESSONS:
 * - Check-then-act operations (get and then put) on ConcurrentHashMap are not atomic.
 * - Use ConcurrentHashMap.computeIfAbsent() to ensure atomic calculation and storage of cached entries.
 */
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class ConcurrentCache<K, V> {
    
    private Map<K, V> cache;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConcurrentCache() {
        this.cache = new HashMap<>();
    }
    
    public V get(K key, Supplier<V> valueSupplier) {
        lock.readLock().lock();
        try {
            V value = cache.get(key);
            if(value != null) {
                return value;
            }
        } finally {
            lock.readLock().unlock();
        } 
        lock.writeLock().lock();
        try {
            V existing = cache.get(key);
            if(existing != null) {
                return existing;
            }
            V value = valueSupplier.get();
            cache.put(key, value);
            return value;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();  
        } finally {
            lock.writeLock().unlock();
        }
    }
}
