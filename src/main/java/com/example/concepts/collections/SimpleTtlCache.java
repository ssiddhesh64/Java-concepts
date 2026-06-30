package com.example.concepts.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleTtlCache {
    // private final Map<String, CacheEntry> cache = new HashMap<>();
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMs;

    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SimpleTtlCache(long ttlMs) {
        this.ttlMs = ttlMs;
    }

    // public synchronized void put(String key, String value) {
    // cache.put(key, new CacheEntry(value, System.currentTimeMillis() + ttlMs));
    // }

    public void put(String key, String value) {
        cache.put(key, new CacheEntry(value, System.currentTimeMillis() + ttlMs));
    }

    // public synchronized String get(String key) {
    // CacheEntry entry = cache.get(key);
    // if (entry == null) {
    // return null;
    // }
    // if (System.currentTimeMillis() > entry.expiryTime) {
    // cache.remove(key); // Evict expired
    // return null;
    // }
    // return entry.value;
    // }

    public String get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (System.currentTimeMillis() > entry.expiryTime) {
            cache.remove(key); // Evict expired
            return null;
        }
        return entry.value;
    }

    public void startCleanupTask() {
        scheduler.scheduleAtFixedRate(() -> cleanUp(), 30, 30, TimeUnit.SECONDS);
    }

    public void cleanUp() {
        cache.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getValue().expiryTime());
    }

    void shutdown() {
        scheduler.shutdown();
    }

    // A background cleanup task that runs in a loop
    // public void startCleanupTask() {
    // new Thread(() -> {
    // while (true) {
    // try {
    // Thread.sleep(5000);
    // synchronized (this) {
    // for (String key : cache.keySet()) {
    // CacheEntry entry = cache.get(key);
    // if (entry != null && System.currentTimeMillis() > entry.expiryTime) {
    // cache.remove(key); // CRASH HERE!
    // }
    // }
    // }
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // break;
    // }
    // }
    // }).start();
    // }

    // private static class CacheEntry {
    // final String value;
    // final long expiryTime;

    // CacheEntry(String value, long expiryTime) {
    // this.value = value;
    // this.expiryTime = expiryTime;
    // }
    // }

    record CacheEntry(String value, long expiryTime) {
    }

}