package com.example.concepts.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleCircuitBreakerTest {

    private SimpleCircuitBreaker breaker;

    @BeforeEach
    void setUp() {
        breaker = new SimpleCircuitBreaker();
    }

    @Test
    void testExecuteSuccessfulTask() throws Exception {
        String result = breaker.executeOptimized(() -> "Success");
        assertThat(result).isEqualTo("Success");
    }

    @Test
    void testTransitionsToOpenAfterThresholdFailures() {
        // Trigger 3 failures to reach threshold (threshold = 3)
        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(
                            () ->
                                    breaker.executeOptimized(
                                            () -> {
                                                throw new RuntimeException("Task Failed");
                                            }))
                    .isInstanceOf(RuntimeException.class);
        }

        // The 4th call should immediately fail because the circuit is OPEN
        assertThatThrownBy(() -> breaker.executeOptimized(() -> "Should not run"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Circuit is OPEN. Fast failing request.");
    }

    @Test
    void testHalfOpenTransitionAndRecovery() throws Exception {
        // Break the circuit
        for (int i = 0; i < 3; i++) {
            try {
                breaker.executeOptimized(
                        () -> {
                            throw new RuntimeException("Fail");
                        });
            } catch (Exception ignored) {
            }
        }

        // Verify it is OPEN
        assertThatThrownBy(() -> breaker.executeOptimized(() -> "Fail fast"))
                .hasMessageContaining("Circuit is OPEN");

        // Backdate the last failure time using reflection on the AtomicLong
        backdateFailureTime(6000); // retryTimeoutMs is 5000ms

        // Next request should attempt to probe (HALF_OPEN) and succeed, closing the circuit
        String result = breaker.executeOptimized(() -> "Recovered");
        assertThat(result).isEqualTo("Recovered");

        // Subsequent requests should proceed normally in CLOSED state
        assertThat(breaker.executeOptimized(() -> "Normal")).isEqualTo("Normal");
    }

    @Test
    void testHalfOpenTransitionAndReopenOnFailure() throws Exception {
        // Break the circuit
        for (int i = 0; i < 3; i++) {
            try {
                breaker.executeOptimized(
                        () -> {
                            throw new RuntimeException("Fail");
                        });
            } catch (Exception ignored) {
            }
        }

        // Backdate failure time
        backdateFailureTime(6000);

        // Next request in HALF_OPEN fails, which should reopen the circuit
        assertThatThrownBy(
                        () ->
                                breaker.executeOptimized(
                                        () -> {
                                            throw new RuntimeException("Probe Fail");
                                        }))
                .hasMessageContaining("Circuit opened due to probe failure");

        // Verify it is OPEN again and fast-fails
        assertThatThrownBy(() -> breaker.executeOptimized(() -> "Should not run"))
                .hasMessageContaining("Circuit is OPEN. Fast failing request.");
    }

    private void backdateFailureTime(long msAgo) throws Exception {
        Field lastFailureTimeField = SimpleCircuitBreaker.class.getDeclaredField("lastFailureTime");
        lastFailureTimeField.setAccessible(true);
        AtomicLong lastFailureTime = (AtomicLong) lastFailureTimeField.get(breaker);
        lastFailureTime.set(System.currentTimeMillis() - msAgo);
    }
}
