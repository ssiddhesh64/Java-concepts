package com.example.concepts.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenBucketRateLimiterTest {

    private TokenBucketRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        // Capacity = 3, refill interval = 1000ms
        rateLimiter = new TokenBucketRateLimiter(3, 1000);
    }

    @Test
    void testBasicRateLimiting() {
        // First 3 requests should be allowed
        assertThat(rateLimiter.allowRequest()).isTrue();
        assertThat(rateLimiter.allowRequest()).isTrue();
        assertThat(rateLimiter.allowRequest()).isTrue();

        // 4th request should be blocked
        assertThat(rateLimiter.allowRequest()).isFalse();
    }

    @Test
    void testTokenRefillByBackdating() throws Exception {
        // Consume all tokens
        rateLimiter.allowRequest();
        rateLimiter.allowRequest();
        rateLimiter.allowRequest();
        assertThat(rateLimiter.allowRequest()).isFalse();

        // Backdate the state by 1000ms (1 refill interval)
        backdateState(1000);

        // One token should have refilled and be allowed
        assertThat(rateLimiter.allowRequest()).isTrue();
        // Next should be blocked
        assertThat(rateLimiter.allowRequest()).isFalse();
    }

    @Test
    void testTokenRefillCapsAtCapacity() throws Exception {
        // Backdate the state by 10 seconds (10 intervals)
        backdateState(10000);

        // Should only allow up to capacity (3), not more
        assertThat(rateLimiter.allowRequest()).isTrue();
        assertThat(rateLimiter.allowRequest()).isTrue();
        assertThat(rateLimiter.allowRequest()).isTrue();
        assertThat(rateLimiter.allowRequest()).isFalse();
    }

    @Test
    void testConcurrentRequests() throws Exception {
        // Capacity = 100
        TokenBucketRateLimiter concurrentLimiter = new TokenBucketRateLimiter(100, 1000);
        int numThreads = 50;
        int requestsPerThread = 5; // 250 requests total, only 100 should succeed

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger allowedCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(
                    () -> {
                        try {
                            latch.await();
                            for (int j = 0; j < requestsPerThread; j++) {
                                if (concurrentLimiter.allowRequest()) {
                                    allowedCount.incrementAndGet();
                                }
                            }
                        } catch (InterruptedException ignored) {
                        }
                    });
        }

        // Start all threads at once
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        assertThat(allowedCount.get()).isEqualTo(100);
    }

    private void backdateState(long msAgo) throws Exception {
        Field stateField = TokenBucketRateLimiter.class.getDeclaredField("state");
        stateField.setAccessible(true);
        @SuppressWarnings("unchecked")
        AtomicReference<Object> stateRef = (AtomicReference<Object>) stateField.get(rateLimiter);

        Object currentState = stateRef.get();
        Field availableTokensField = currentState.getClass().getDeclaredField("availableTokens");
        Field lastRefillTimeNanosField =
                currentState.getClass().getDeclaredField("lastRefillTimeNanos");
        availableTokensField.setAccessible(true);
        lastRefillTimeNanosField.setAccessible(true);

        long currentTokens = (long) availableTokensField.get(currentState);
        long currentNanos = (long) lastRefillTimeNanosField.get(currentState);

        // Create a new BucketState with backdated time
        Object backdatedState =
                currentState
                        .getClass()
                        .getDeclaredConstructor(long.class, long.class)
                        .newInstance(currentTokens, currentNanos - (msAgo * 1_000_000L));

        stateRef.set(backdatedState);
    }
}
