import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ResilientStreamCollector {

    // Custom functional interface that throws checked exceptions
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    /**
     * Container to hold partitioned results of successes and exceptions.
     */
    public static class StreamPartitionResult<R> {
        private final List<R> successes = new ArrayList<>();
        private final List<Throwable> failures = new ArrayList<>();

        public List<R> getSuccesses() {
            return successes;
        }

        public List<Throwable> getFailures() {
            return failures;
        }

        @Override
        public String toString() {
            return "StreamPartitionResult{" +
                    "successes=" + successes +
                    ", failures=" + failures.stream().map(Throwable::getMessage).collect(Collectors.toList()) +
                    '}';
        }
    }

    // Dummy method that simulates reading file contents and throws IOException
    public static String readFile(String filename) throws IOException {
        if ("file1.txt".equals(filename)) {
            return "Hello from File 1";
        } else if ("file2.txt".equals(filename)) {
            return "Welcome to File 2";
        } else {
            throw new NoSuchFileException("File " + filename + " does not exist");
        }
    }

    /**
     * Implement this method to create a custom Collector:
     * 
     * 1. The collector should map each input element using the provided `mapper`.
     * 2. If mapping succeeds, the resulting value should be added to the `successes` list.
     * 3. If mapping throws any Exception (including checked exceptions), the exception should 
     *    be caught and added to the `failures` list.
     * 4. The collector must be thread-safe for parallel stream processing (implement the combiner).
     * 
     * @param mapper throwing mapping function
     * @return a custom Collector accumulating elements into StreamPartitionResult
     */
    public static <T, R> Collector<T, ?, StreamPartitionResult<R>> partitioningCollector(
            ThrowingFunction<T, R, ?> mapper) {
        // TODO: Implement the custom partitioning collector using Collector.of()
        return Collector.of(StreamPartitionResult<R>::new,
            (result, element) -> {
                try {
                    R value = mapper.apply(element);
                    result.getSuccesses().add(value);
                } catch (Exception e) {
                    result.getFailures().add(e);
                }
            },
            (res1, res2) -> {
                res1.getSuccesses().addAll(res2.getSuccesses());
                res1.getFailures().addAll(res2.getFailures());
                return res1;
            }
        );
    }

    // Test Harness
    public static void main(String[] args) {
        System.out.println("=== Starting Resilient Stream Collector Tests ===");

        List<String> filesToRead = List.of("file1.txt", "missing.txt", "file2.txt", "corrupt.txt");

        // Execute streaming pipeline using your custom collector
        StreamPartitionResult<String> result = filesToRead.stream()
                .collect(partitioningCollector(ResilientStreamCollector::readFile));

        System.out.println("\nCollector Output: " + result);

        // Validations
        List<String> successes = result.getSuccesses();
        List<Throwable> failures = result.getFailures();

        boolean checkSuccessCount = successes.size() == 2;
        boolean checkSuccessValues = successes.contains("Hello from File 1") && successes.contains("Welcome to File 2");
        boolean checkFailureCount = failures.size() == 2;
        boolean checkFailureTypes = failures.stream().allMatch(t -> t instanceof NoSuchFileException);

        if (checkSuccessCount && checkSuccessValues && checkFailureCount && checkFailureTypes) {
            System.out.println("\nSUCCESS: Resilient partitioning collector implemented perfectly!");
        } else {
            System.err.println("\nFAILURE: Verification failed.");
            System.err.println("  Success count == 2: " + checkSuccessCount);
            System.err.println("  Success values correct: " + checkSuccessValues);
            System.err.println("  Failure count == 2: " + checkFailureCount);
            System.err.println("  Failures are NoSuchFileException: " + checkFailureTypes);
        }
    }
}
