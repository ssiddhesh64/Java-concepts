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
