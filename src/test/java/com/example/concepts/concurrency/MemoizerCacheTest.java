package com.example.concepts.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemoizerCacheTest {

    private ExecutorService executor;
    private MemoizerCache<String, String> cache;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
        cache = new MemoizerCache<>(executor);
    }

    @Test
    void testBasicCacheHit() {
        AtomicInteger computations = new AtomicInteger(0);

        String result1 =
                cache.get(
                        "key1",
                        k -> {
                            computations.incrementAndGet();
                            return "value1";
                        });

        String result2 =
                cache.get(
                        "key1",
                        k -> {
                            computations.incrementAndGet();
                            return "value1";
                        });

        assertThat(result1).isEqualTo("value1");
        assertThat(result2).isEqualTo("value1");
        assertThat(computations.get()).isEqualTo(1); // Computed only once
    }

    @Test
    void testCacheStampedePrevention() throws InterruptedException {
        AtomicInteger computations = new AtomicInteger(0);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            new Thread(
                            () -> {
                                try {
                                    startLatch.await();
                                    cache.get(
                                            "key2",
                                            k -> {
                                                try {
                                                    Thread.sleep(100); // Simulate slow calculation
                                                } catch (InterruptedException e) {
                                                    Thread.currentThread().interrupt();
                                                }
                                                computations.incrementAndGet();
                                                return "value2";
                                            });
                                } catch (InterruptedException ignored) {
                                } finally {
                                    finishLatch.countDown();
                                }
                            })
                    .start();
        }

        // Release all threads at once to request "key2"
        startLatch.countDown();
        finishLatch.await(5, TimeUnit.SECONDS);

        assertThat(computations.get())
                .isEqualTo(1); // Only 1 thread actually executed the computation
    }

    @Test
    void testEvictsOnException() {
        AtomicInteger computations = new AtomicInteger(0);

        // First call fails
        assertThatThrownBy(
                        () ->
                                cache.get(
                                        "failedKey",
                                        k -> {
                                            computations.incrementAndGet();
                                            throw new RuntimeException("Compute failed");
                                        }))
                .isInstanceOf(RuntimeException.class);

        // Second call succeeds
        String result =
                cache.get(
                        "failedKey",
                        k -> {
                            computations.incrementAndGet();
                            return "recovered";
                        });

        assertThat(result).isEqualTo("recovered");
        assertThat(computations.get()).isEqualTo(2); // Retried and executed again
    }
}
