package com.example.concepts.streams;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import org.junit.jupiter.api.Test;

class ResilientStreamCollectorTest {

    @Test
    void testPartitioningCollectorSuccessAndFailure() {
        List<String> filenames =
                Arrays.asList("file1.txt", "file2.txt", "missing.txt", "file1.txt");

        Collector<String, ?, ResilientStreamCollector.StreamPartitionResult<String>> collector =
                ResilientStreamCollector.partitioningCollector(ResilientStreamCollector::readFile);

        ResilientStreamCollector.StreamPartitionResult<String> result =
                filenames.stream().collect(collector);

        assertThat(result.getSuccesses())
                .containsExactly("Hello from File 1", "Welcome to File 2", "Hello from File 1");
        assertThat(result.getFailures()).hasSize(1);
        assertThat(result.getFailures().get(0)).isInstanceOf(NoSuchFileException.class);
    }

    @Test
    void testPartitioningCollectorAllSuccess() {
        List<String> filenames = Arrays.asList("file1.txt", "file2.txt");

        Collector<String, ?, ResilientStreamCollector.StreamPartitionResult<String>> collector =
                ResilientStreamCollector.partitioningCollector(ResilientStreamCollector::readFile);

        ResilientStreamCollector.StreamPartitionResult<String> result =
                filenames.stream().collect(collector);

        assertThat(result.getSuccesses()).hasSize(2);
        assertThat(result.getFailures()).isEmpty();
    }

    @Test
    void testPartitioningCollectorParallelStream() {
        List<String> filenames =
                Arrays.asList("file1.txt", "missing1.txt", "file2.txt", "missing2.txt");

        Collector<String, ?, ResilientStreamCollector.StreamPartitionResult<String>> collector =
                ResilientStreamCollector.partitioningCollector(ResilientStreamCollector::readFile);

        // Run as parallel stream to exercise the combiner logic
        ResilientStreamCollector.StreamPartitionResult<String> result =
                filenames.parallelStream().collect(collector);

        assertThat(result.getSuccesses())
                .containsExactlyInAnyOrder("Hello from File 1", "Welcome to File 2");
        assertThat(result.getFailures()).hasSize(2);
    }
}
