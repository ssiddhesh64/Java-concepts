package com.example.concepts.concurrency;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleConcurrentStack<T> {
    // private Node<T> head;

    private AtomicReference<Node<T>> atomicHead = new AtomicReference<>();

    // Heavily bottlenecked under high write contention!
    // public synchronized void push(T value) {
    // Node<T> newHead = new Node<>(value);
    // newHead.next = head;
    // head = newHead;
    // }

    public void pushOptimised(T value) {
        Node<T> newHead = new Node<>(value);
        Node<T> oldHead;
        do {
            oldHead = atomicHead.get();
            newHead.next = oldHead;
        } while (!atomicHead.compareAndSet(oldHead, newHead));
    }

    // Only 1 thread can read/pop at a time.
    // public synchronized T pop() {
    // if (head == null) {
    // return null;
    // }
    // T value = head.value;
    // head = head.next;
    // return value;
    // }

    public T popOptimised() {
        Node<T> currentHead;
        Node<T> newHead;
        do {
            currentHead = atomicHead.get();
            if (currentHead == null) {
                return null;
            }
            newHead = currentHead.next;
        } while (!atomicHead.compareAndSet(currentHead, newHead));

        return currentHead.value;
    }

    private static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

}