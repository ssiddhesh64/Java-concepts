package com.example.concepts.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThreeThreadsAlternateTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        resetStaticState();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testThreadAlternationOutput() throws Exception {
        // Run the main method
        ThreeThreadsAlternate.main(new String[0]);

        // Wait for the counter to exceed MAX (100) or timeout (2 seconds)
        long startTime = System.currentTimeMillis();
        while (getCounterVal() <= 100 && (System.currentTimeMillis() - startTime) < 2000) {
            Thread.sleep(50);
        }

        String output = outContent.toString();
        assertThat(output).isNotEmpty();

        // Parse and check that the sequence of thread IDs alternates correctly: 0, 1, 2, 0, 1, 2...
        String[] lines = output.split(System.lineSeparator());
        int expectedId = 0;
        int expectedCounter = 1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            // Line format: "Thread id X Y"
            assertThat(line).startsWith("Thread id ");
            String[] parts = line.split(" ");
            assertThat(parts).hasSize(4);

            int threadId = Integer.parseInt(parts[2]);
            int counterVal = Integer.parseInt(parts[3]);

            assertThat(threadId).isEqualTo(expectedId);
            assertThat(counterVal).isEqualTo(expectedCounter);

            expectedId = (expectedId + 1) % 3;
            expectedCounter++;
        }

        // Verify we reached 100
        assertThat(expectedCounter - 1).isEqualTo(100);
    }

    private void resetStaticState() throws Exception {
        Field counterField = ThreeThreadsAlternate.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        counterField.set(null, 1);

        ThreeThreadsAlternate.semaphores[0].drainPermits();
        ThreeThreadsAlternate.semaphores[0].release(1);

        ThreeThreadsAlternate.semaphores[1].drainPermits();
        ThreeThreadsAlternate.semaphores[2].drainPermits();
    }

    private int getCounterVal() throws Exception {
        Field counterField = ThreeThreadsAlternate.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        return (int) counterField.get(null);
    }
}
