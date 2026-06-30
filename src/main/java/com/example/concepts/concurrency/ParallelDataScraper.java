package com.example.concepts.concurrency;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ParallelDataScraper {
    private final HttpClient client = HttpClient.newHttpClient();

    public void scrapeUrls(List<String> urls) {
        // Parallel stream to process URLs in parallel
        urls.parallelStream()
                .map(this::fetchData) // Blocks the executing thread
                .forEach(System.out::println);
    }

    private String fetchData(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            // Blocking HTTP Call
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "Failed to fetch: " + url;
        }
    }

    public void scrapeUrlsAsync(List<String> urls) {
        List<CompletableFuture<Void>> futures = urls.stream() // No need for parallelStream now!
                .map(url -> fetchDataAsync(url)
                        .thenAccept(System.out::println)
                        .exceptionally(ex -> {
                            System.out.println("Failed to fetch: " + url);
                            return null;
                        }))
                .toList(); // Terminal operation triggers stream execution!

        // Wait for all HTTP requests to complete asynchronously
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private CompletableFuture<String> fetchDataAsync(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        // Native, non-blocking asynchronous call!
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

}