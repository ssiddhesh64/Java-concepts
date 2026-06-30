package com.example.concepts.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

public class ReadHeavyCache {
    private final Map<String, String> cache = new HashMap<>();
    private final StampedLock lock = new StampedLock();

    public String get(String key) {
        // Try an optimistic read first (does not acquire a read-lock)
        long stamp = lock.tryOptimisticRead();
        String value = cache.get(key);

        // Validate if a write occurred between tryOptimisticRead and reading the value
        if (!lock.validate(stamp)) {
            // Validation failed -> acquire a fully blocking read-lock
            stamp = lock.readLock();
            try {
                value = cache.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return value;
    }

    public void put(String key, String value) {
        long stamp = lock.writeLock();
        try {
            cache.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

}