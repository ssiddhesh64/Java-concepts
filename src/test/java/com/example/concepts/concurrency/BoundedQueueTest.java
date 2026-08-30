package com.example.concepts.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class BoundedQueueTest {

    @Test
    void testBasicPutAndTake() throws InterruptedException {
        BoundedQueue<Integer> queue = new BoundedQueue<>(3);
        queue.put(1);
        queue.put(2);
        queue.put(3);

        assertThat(queue.take()).isEqualTo(1);
        assertThat(queue.take()).isEqualTo(2);
        assertThat(queue.take()).isEqualTo(3);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testPutBlocksWhenFull() throws InterruptedException {
        BoundedQueue<Integer> queue = new BoundedQueue<>(2);
        queue.put(1);
        queue.put(2);

        CountDownLatch latch = new CountDownLatch(1);
        Thread producer =
                new Thread(
                        () -> {
                            try {
                                queue.put(3);
                                latch.countDown();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });

        producer.start();
        boolean completed = latch.await(200, TimeUnit.MILLISECONDS);
        assertThat(completed).isFalse();

        assertThat(queue.take()).isEqualTo(1);

        boolean completedAfterTake = latch.await(1, TimeUnit.SECONDS);
        assertThat(completedAfterTake).isTrue();
        assertThat(queue.take()).isEqualTo(2);
        assertThat(queue.take()).isEqualTo(3);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testTakeBlocksWhenEmpty() throws InterruptedException {
        BoundedQueue<Integer> queue = new BoundedQueue<>(2);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Integer> result = new AtomicReference<>();

        Thread consumer =
                new Thread(
                        () -> {
                            try {
                                result.set(queue.take());
                                latch.countDown();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });

        consumer.start();
        boolean completed = latch.await(200, TimeUnit.MILLISECONDS);
        assertThat(completed).isFalse();

        queue.put(42);

        boolean completedAfterPut = latch.await(1, TimeUnit.SECONDS);
        assertThat(completedAfterPut).isTrue();
        assertThat(result.get()).isEqualTo(42);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInterruptsArePropagated() throws InterruptedException {
        BoundedQueue<Integer> queue = new BoundedQueue<>(1);
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch interrupted = new CountDownLatch(1);

        Thread thread =
                new Thread(
                        () -> {
                            try {
                                started.countDown();
                                queue.take();
                            } catch (InterruptedException e) {
                                interrupted.countDown();
                            }
                        });

        thread.start();
        started.await();
        Thread.sleep(100);

        thread.interrupt();

        boolean interruptedOk = interrupted.await(1, TimeUnit.SECONDS);
        assertThat(interruptedOk).isTrue();
    }
}
