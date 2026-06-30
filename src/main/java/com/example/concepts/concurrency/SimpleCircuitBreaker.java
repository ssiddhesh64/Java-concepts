package com.example.concepts.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleCircuitBreaker {
    // private String state = "CLOSED"; // CLOSED, OPEN, HALF_OPEN
    // private int failureCount = 0;
    private final int failureThreshold = 3;
    private final long retryTimeoutMs = 5000;
    // private long lastFailureTime = 0;

    private AtomicReference<State> state = new AtomicReference<>(State.CLOSED); // CLOSED, OPEN, HALF_OPEN
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0L);

    // Massively bottlenecked under high load!
    // public synchronized String execute(Supplier<String> task) throws Exception {
    // if (state.equals("OPEN")) {
    // // Check if the retry timeout has passed
    // if (System.currentTimeMillis() - lastFailureTime > retryTimeoutMs) {
    // state = "HALF_OPEN";
    // System.out.println("Circuit HALF-OPEN. Testing gateway...");
    // } else {
    // throw new RuntimeException("Circuit is OPEN. Fast failing request.");
    // }
    // }

    // try {
    // CompletableFuture<String> res = CompletableFuture.supplyAsync(() ->
    // task.get());
    // String result = task.get();
    // if (state.equals("HALF_OPEN")) {
    // state = "CLOSED";
    // failureCount = 0;
    // System.out.println("Circuit CLOSED. Gateway recovered.");
    // }
    // return result;
    // } catch (Exception e) {
    // failureCount++;
    // lastFailureTime = System.currentTimeMillis();
    // if (failureCount >= failureThreshold || state.equals("HALF_OPEN")) {
    // state = "OPEN";
    // System.out.println("Circuit OPENED due to failures!");
    // }
    // throw e;
    // }
    // }

    public String executeOptimized(Supplier<String> task) throws Exception {

        State currState = state.get();
        if (currState == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime.get() > retryTimeoutMs) {
                if (!state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                    throw new RuntimeException("Another thread is already probing");
                }
            } else {
                throw new RuntimeException("Circuit is OPEN. Fast failing request.");
            }
        } else if (currState == State.HALF_OPEN) {
            throw new RuntimeException("Circuit is HALF_OPEN. Probing in progress...");
        }

        try {
            String res = task.get();
            if (currState == State.HALF_OPEN) {
                state.compareAndSet(State.HALF_OPEN, State.CLOSED);
                failureCount.set(0);
                System.out.println("Circuit CLOSED. Gateway recovered.");
            }
            return res;
        } catch (Exception e) {
            failureCount.incrementAndGet();
            lastFailureTime.set(System.currentTimeMillis());

            State s = state.get();
            if (s == State.HALF_OPEN) {
                state.compareAndSet(State.HALF_OPEN, State.OPEN);
                throw new RuntimeException("Circuit opened due to probe failure", e);
            } else if (s == State.CLOSED && failureCount.get() >= failureThreshold) {
                state.compareAndSet(State.CLOSED, State.OPEN);
                throw new RuntimeException("Circuit opened due to excessive failures", e);
            }
            throw e;
        }
    }

    // Nested helper class to prevent namespace conflicts
    static enum State {
    OPEN,
    HALF_OPEN,
    CLOSED;
}

}