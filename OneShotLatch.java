/**
 * CONCEPT TAUGHT: Custom Synchronizer with Wait and Notify
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates primitive thread coordination using wait() and notifyAll() inside synchronized methods.
 * 
 * KEY LESSONS:
 * - Always check condition variables in a while-loop (not an if-statement) to handle spurious wakeups.
 * - Call notifyAll() rather than notify() to avoid missed signals.
 */
public class OneShotLatch {
    
    private boolean open = false;

    public synchronized void await() throws InterruptedException {
        while(!open) {
            wait();
        }
    }

    public synchronized void signal() {
        open = true;
        notifyAll();
    }
}
