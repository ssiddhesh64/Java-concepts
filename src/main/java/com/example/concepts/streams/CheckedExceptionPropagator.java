package com.example.concepts.streams;

/**
 * CONCEPT TAUGHT: Checked Exception Propagation in Streams
 * 
 * WHY THIS WAS WRITTEN:
 * - Solves the limitation where Stream lambda expressions cannot throw checked exceptions by wrapping and unwrapping exceptions.
 * 
 * KEY LESSONS:
 * - Checked exceptions inside streams must be wrapped in an unchecked exception (like a custom RuntimeException).
 * - Catch the wrapper exception outside the stream pipeline and unwrap/rethrow the original checked exception.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CheckedExceptionPropagator {

    // Custom functional interfaces
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    // A custom runtime wrapper exception used internally only
    private static class WrappedException extends RuntimeException {
        public WrappedException(Throwable cause) {
            super(cause);
        }
    }

    // Dummy method that throws a checked ParseException
    public static Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        if (dateStr == null) {
            throw new NullPointerException("dateStr cannot be null");
        }
        return sdf.parse(dateStr);
    }

    /**
     * Requirement 1:
     * Map the items using the throwing mapper. If any item throws the checked exception E, 
     * immediately abort the stream execution and throw that checked exception E to the caller.
     * 
     * Rules:
     * - Unchecked exceptions (RuntimeException) thrown by the mapper should be propagated as-is
     *   without being wrapped in E.
     * - The method signature correctly declares `throws E`.
     */
    public static <T, R, E extends Exception> List<R> mapAndCollect(
            Collection<T> items, 
            ThrowingFunction<T, R, E> mapper) throws E {
        // TODO: Implement stream mapping and exception propagation
        try {
            return items.stream().map((item) -> {
                try {
                    return mapper.apply(item);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new WrappedException(e);
                }
            }).collect(Collectors.toList());
        } catch (WrappedException e) {
            @SuppressWarnings("unchecked")
            E cause = (E) e.getCause();
            throw cause;
        }
    }

    /**
     * Requirement 2:
     * Run the consumer over the items. If any item throws the checked exception E, 
     * immediately abort the loop execution and throw that checked exception E to the caller.
     * 
     * Rules:
     * - Unchecked exceptions (RuntimeException) thrown by the consumer should be propagated as-is.
     * - The method signature correctly declares `throws E`.
     */
    public static <T, E extends Exception> void forEachWithException(
            Collection<T> items, 
            ThrowingConsumer<T, E> consumer) throws E {
        // TODO: Implement exception-aware forEach
        try {
            items.forEach((item) -> {
                try {
                    consumer.accept(item);
                } catch (RuntimeException e) {
                    throw e;
                } catch(Exception e) {
                    throw new WrappedException(e);
                }
            });
        } catch (WrappedException e) {
            @SuppressWarnings("unchecked")
            E cause = (E) e.getCause();
            throw cause;
        }
    }

    // Test Harness
    public static void main(String[] args) {
        System.out.println("=== Starting Checked Exception Propagator Tests ===");

        List<String> dates = List.of("2026-06-25", "invalid-date-format", "2026-06-26");

        // Test 1: Map and Collect - Checked Exception Catching
        System.out.println("\n--- Test 1: Map and Collect (Checked Exception Catching) ---");
        try {
            // Note how the compiler permits catching ParseException directly because mapAndCollect declares throws E!
            List<Date> results = mapAndCollect(dates, CheckedExceptionPropagator::parseDate);
            System.err.println("Test 1 Failed: Expected ParseException but succeeded. Results: " + results);
        } catch (ParseException e) {
            System.out.println("SUCCESS: Caught raw checked ParseException directly!");
        } catch (Exception e) {
            System.err.println("FAILURE: Caught wrong exception type: " + e.getClass().getName());
        }

        // Test 2: Map and Collect - Runtime Exception Propagation
        System.out.println("\n--- Test 2: Map and Collect (RuntimeException Propagation) ---");
        try {
            List<String> itemsWithNull = Arrays.asList("2026-06-25", null);
            mapAndCollect(itemsWithNull, CheckedExceptionPropagator::parseDate);
            System.err.println("Test 2 Failed: Expected NullPointerException but succeeded.");
        } catch (NullPointerException e) {
            System.out.println("SUCCESS: NullPointerException propagated without wrapping!");
        } catch (Throwable t) {
            System.err.println("FAILURE: Expected NullPointerException but got: " + t.getClass().getName());
        }

        // Test 3: forEachWithException
        System.out.println("\n--- Test 3: forEachWithException ---");
        List<String> testFiles = List.of("fileA.txt", "error_file.txt", "fileB.txt");
        try {
            forEachWithException(testFiles, filename -> {
                if ("error_file.txt".equals(filename)) {
                    throw new java.io.IOException("Disk read error for " + filename);
                }
                System.out.println("Processed: " + filename);
            });
            System.err.println("Test 3 Failed: Expected IOException but succeeded.");
        } catch (java.io.IOException e) {
            System.out.println("SUCCESS: Caught checked IOException from consumer loop!");
        } catch (Exception e) {
            System.err.println("FAILURE: Caught wrong exception type: " + e.getClass().getName());
        }
    }

}