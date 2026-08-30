package com.example.concepts.concurrency;

import java.util.concurrent.Semaphore;

public class ThreeThreadsAlternate {

    private static int counter = 1;
    private static final int MAX_THREADS = 3;
    private static final int MAX = 100;
    public static final Semaphore[] semaphores = {
            new Semaphore(1),
            new Semaphore(0),
            new Semaphore(0)
    };

    public static void main(String[] args) {

        for (int i = 0; i < MAX_THREADS; i++) {
            final int currentThreadIdx = i;
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        semaphores[currentThreadIdx].acquire();

                        if (counter > MAX) {
                            semaphores[(currentThreadIdx + 1) % MAX_THREADS].release();
                            break;
                        }
                        System.out.println("Thread id " + currentThreadIdx + " " + counter);
                        counter++;
                        semaphores[(currentThreadIdx + 1) % MAX_THREADS].release();

                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
            t.start();
        }
    }
}
