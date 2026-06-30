package com.example.concepts.concurrency;

/**
 * CONCEPT TAUGHT: Explicit ReentrantLock and Condition Coordination
 * 
 * WHY THIS WAS WRITTEN:
 * - Implements a thread-safe bounded queue with producers and consumers coordinated by Condition variables.
 * 
 * KEY LESSONS:
 * - Always call lock.lock() outside the try block.
 * - Check conditions in a while-loop (not an if) to handle spurious wakeups.
 * - Propagate InterruptedException to callers in blocking methods.
 * - Use signal() instead of signalAll() to optimize context switching.
 */
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class BoundedQueue<T> {

    Queue<T> queue = new LinkedList<T>();
    ReentrantLock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    int capacity;

    BoundedQueue(int cap) {
        this.capacity = cap;
    }

    void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.add(item);
            notEmpty.signal();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            lock.unlock();
        }
    }

    T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == 0) {
                notEmpty.await();
            }
            T res = queue.poll();
            notFull.signal();
            return res;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            lock.unlock();
        }
    }

}