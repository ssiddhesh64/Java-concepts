import java.util.concurrent.atomic.AtomicReference;

public class TokenBucketRateLimiter {
    // private final int maxTokens;
    // private final long refillRateMs;
    // private int currentTokens;
    // private long lastRefillTime;

    // public TokenBucketRateLimiter(int maxTokens, long refillRateMs) {
    // this.maxTokens = maxTokens;
    // this.refillRateMs = refillRateMs;
    // this.currentTokens = maxTokens;
    // this.lastRefillTime = System.currentTimeMillis();
    // }

    // Heavy bottleneck in an API Gateway! Only 1 thread can request at a time.
    // public synchronized boolean allowRequest() {
    // refill();
    // if (currentTokens > 0) {
    // currentTokens--;
    // return true;
    // }
    // return false;
    // }

    // private void refill() {
    // long now = System.currentTimeMillis();
    // if (now > lastRefillTime + refillRateMs) {
    // long periods = (now - lastRefillTime) / refillRateMs;
    // currentTokens = Math.min(maxTokens, currentTokens + (int) periods);

    // // This introduces truncation drift!
    // lastRefillTime = now;
    // }
    // }

    private static final class BucketState {
        final long availableTokens;
        final long lastRefillTimeNanos;

        BucketState(long availableTokens, long lastRefillTimeNanos) {
            this.availableTokens = availableTokens;
            this.lastRefillTimeNanos = lastRefillTimeNanos;
        }
    }

    private final long capacity;
    private final long refillIntervalNanos;

    private final AtomicReference<BucketState> state;

    public TokenBucketRateLimiter(long capacity,
            long refillIntervalMillis) {

        this.capacity = capacity;
        this.refillIntervalNanos = refillIntervalMillis * 1_000_000L;

        long now = System.nanoTime();

        this.state = new AtomicReference<>(
                new BucketState(capacity, now));
    }

    public boolean allowRequest() {

        while (true) {

            BucketState current = state.get();

            long now = System.nanoTime();

            long elapsed = now - current.lastRefillTimeNanos;

            long tokensToAdd = elapsed / refillIntervalNanos;

            long newTokens = Math.min(
                    capacity,
                    current.availableTokens + tokensToAdd);

            // Preserve fractional elapsed time
            long newRefillTime = current.lastRefillTimeNanos
                    + tokensToAdd * refillIntervalNanos;

            if (newTokens == 0) {
                return false;
            }

            BucketState updated = new BucketState(
                    newTokens - 1,
                    newRefillTime);

            if (state.compareAndSet(current, updated)) {
                return true;
            }

            // another thread updated state
            // retry
        }
    }
}
