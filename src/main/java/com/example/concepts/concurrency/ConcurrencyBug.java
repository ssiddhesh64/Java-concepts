package com.example.concepts.concurrency;

/**
 * CONCEPT TAUGHT: Thread-Safety in Shared Collections
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates the dangers of concurrent writes to a non-thread-safe ArrayList and shows how to resolve it.
 * 
 * KEY LESSONS:
 * - Concurrent writes to standard collections lead to data corruption, lost updates, or sizing errors.
 * - Use Collections.synchronizedList() or CopyOnWriteArrayList depending on the read/write load profile.
 */
import java.util.*;
import java.util.concurrent.*;

public class ConcurrencyBug {

    // This is anti pattern since write heavy tasks would be very slow
    // as the list will be copied every time
    // private static final List<Integer> list = new CopyOnWriteArrayList<>();

    // Better alternative
    private static final List<Integer> list = Collections.synchronizedList(new ArrayList<>());
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                list.add(i);
            }
        };

        executor.submit(task);
        executor.submit(task);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("List size: " + list.size());
    }

}