package com.example.concepts.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SilentAuditLogger {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void logAudit(String event) {
        // Runs task in background
        CompletableFuture.runAsync(() -> {
            saveToDatabase(event); // This throws RuntimeException when DB is down
        }, executor)
                .exceptionally(ex -> {
                    System.err.println(ex.getMessage());
                    return null;
                });
        // The future is discarded immediately
    }

    private void saveToDatabase(String event) {
        if (event == null || event.contains("error_test")) {
            throw new RuntimeException("Database connection timeout!");
        }
        System.out.println("Saved audit event: " + event);
    }

}