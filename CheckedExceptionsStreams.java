import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CheckedExceptionsStreams {

    // A custom functional interface that allows checked exceptions
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    // A custom supplier interface that allows checked exceptions
    @FunctionalInterface
    public interface ThrowingSupplier<T, E extends Exception> {
        T get() throws E;
    }

    /**
     * A wrapper class to represent the result of an operation that might fail.
     * Often referred to as "Either" (Success or Failure) in functional programming.
     */
    public static class Result<T> {
        private final T value;
        private final Throwable failure;

        private Result(T value, Throwable failure) {
            this.value = value;
            this.failure = failure;
        }

        public static <T> Result<T> success(T value) {
            return new Result<>(value, null);
        }

        public static <T> Result<T> failure(Throwable failure) {
            return new Result<>(null, failure);
        }

        public boolean isSuccess() {
            return failure == null;
        }

        public T getValue() {
            return value;
        }

        public Throwable getFailure() {
            return failure;
        }

        @Override
        public String toString() {
            return isSuccess() ? "Success(" + value + ")" : "Failure(" + failure.getMessage() + ")";
        }
    }

    // Dummy method that throws a checked ParseException
    public static Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(dateStr);
    }

    /**
     * Requirement 1:
     * Implement this method to wrap a ThrowingFunction into a standard Java Function.
     * If the throwing function throws a checked exception, it should be caught and
     * wrapped in a RuntimeException.
     */
    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R, ?> throwingFunction) {
        // TODO: Implement standard wrapper
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Requirement 2:
     * Implement this method to wrap a ThrowingFunction into a standard Java Function using "Sneaky Throws".
     * This tricks the compiler into letting you throw a checked exception without wrapping it in a RuntimeException
     * and without declaring it in the throws clause.
     */
    public static <T, R> Function<T, R> sneaky(ThrowingFunction<T, R, ?> throwingFunction) {
        // TODO: Implement sneaky throws wrapper
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch(Exception ex) {
                sneakyThrow(ex);
            }
            return null;
        };
    }

    /**
     * Requirement 3:
     * Implement this method to safely run a ThrowingSupplier and return a Result object
     * indicating success (with value) or failure (with exception).
     */
    public static <T> Result<T> wrapResult(ThrowingSupplier<T, ?> supplier) {
        // TODO: Implement result wrapper
        try {
            T value = supplier.get();
            return Result.success(value);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    // Helper for sneaky throws
    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable t) throws E {
        throw (E) t;
    }

    // Test Harness
    public static void main(String[] args) {
        System.out.println("=== Starting Checked Exception Stream Tests ===");

        List<String> dates = List.of("2026-06-25", "invalid-date-format", "2026-06-26");

        // Test 1: Unchecked Wrapper
        System.out.println("\n--- Test 1: Unchecked Wrapper ---");
        try {
            List<Date> parsedDates = dates.stream()
                .map(unchecked(CheckedExceptionsStreams::parseDate))
                .collect(Collectors.toList());
            System.err.println("Test 1 Failed: Expected exception but succeeded. Result: " + parsedDates);
        } catch (RuntimeException e) {
            System.out.println("Caught Expected RuntimeException: " + e.getClass().getName());
            if (e.getCause() instanceof ParseException) {
                System.out.println("SUCCESS: Cause is ParseException");
            } else {
                System.err.println("FAILURE: Cause is not ParseException but " + e.getCause());
            }
        }

        // Test 2: Sneaky Throws Wrapper
        System.out.println("\n--- Test 2: Sneaky Throws Wrapper ---");
        try {
            List<Date> parsedDates = dates.stream()
                .map(sneaky(CheckedExceptionsStreams::parseDate))
                .collect(Collectors.toList());
            System.err.println("Test 2 Failed: Expected exception but succeeded. Result: " + parsedDates);
        } catch (Throwable t) {
            System.out.println("Caught Exception: " + t.getClass().getName());
            // Assert that it is a ParseException and NOT a RuntimeException
            if (t instanceof ParseException) {
                System.out.println("SUCCESS: Caught raw ParseException!");
            } else {
                System.err.println("FAILURE: Expected raw ParseException but caught: " + t.getClass().getName());
            }
        }

        // Test 3: Result Wrapper (Safe Stream Processing)
        System.out.println("\n--- Test 3: Result Wrapper (Safe Stream Processing) ---");
        List<Result<Date>> results = dates.stream()
            .map(dateStr -> wrapResult(() -> parseDate(dateStr)))
            .collect(Collectors.toList());

        System.out.println("All results: " + results);

        long successCount = results.stream().filter(Result::isSuccess).count();
        long failureCount = results.stream().filter(r -> !r.isSuccess()).count();

        if (successCount == 2 && failureCount == 1) {
            System.out.println("SUCCESS: Processed successes and failures separately");
        } else {
            System.err.println("FAILURE: Expected 2 successes and 1 failure, but got " + successCount + " and " + failureCount);
        }
    }
}
