package com.example.concepts.collections;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SimpleTtlCacheTest {

    private SimpleTtlCache cache;

    @AfterEach
    void tearDown() {
        if (cache != null) {
            cache.shutdown();
        }
    }

    @Test
    void testBasicPutAndGet() {
        cache = new SimpleTtlCache(5000);
        cache.put("key1", "value1");
        assertThat(cache.get("key1")).isEqualTo("value1");
    }

    @Test
    void testGetReturnsNullAfterExpiration() throws InterruptedException {
        // 50ms TTL
        cache = new SimpleTtlCache(50);
        cache.put("key1", "value1");

        // Wait for it to expire
        Thread.sleep(100);

        assertThat(cache.get("key1")).isNull();
    }

    @Test
    void testCleanupEvictsExpiredKeys() throws InterruptedException {
        cache = new SimpleTtlCache(100);
        cache.put("key1", "value1");

        Thread.sleep(150);

        cache.put("key2", "value2");

        // Run passive cleanUp manually
        cache.cleanUp();

        // key1 should be evicted, key2 should remain
        assertThat(cache.get("key1")).isNull();
        assertThat(cache.get("key2")).isEqualTo("value2");
    }
}
