import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleConnectionPool {
    // private final List<Connection> pool = new ArrayList<>();
    private final int maxSize;

    // public SimpleConnectionPool(int maxSize) {
    // this.maxSize = maxSize;
    // for (int i = 0; i < maxSize; i++) {
    // pool.add(new Connection("Connection-" + i));
    // }
    // }

    private final BlockingQueue<Connection> queue;

    public SimpleConnectionPool(int maxSize) {
        this.maxSize = maxSize;
        queue = new LinkedBlockingQueue<>(maxSize);
        for (int i = 0; i < maxSize; i++) {
            try {
                queue.put(new Connection("Connection-" + i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // CATASTROPHIC DEADLOCK BUG HERE!
    // public synchronized Connection acquire() {
    // while (pool.isEmpty()) {
    // try {
    // // Sleep inside a synchronized block holding the lock!
    // Thread.sleep(100);
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // return null;
    // }
    // }
    // return pool.remove(0); // O(N) array shifts!
    // }

    // public synchronized void release(Connection connection) {
    // if (pool.size() < maxSize) {
    // pool.add(connection);
    // }
    // }

    public static class Connection {
        private final String id;

        public Connection(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public Connection acquire() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void release(Connection connection) {
        boolean accepted = queue.offer(connection);
        if (!accepted) {
            System.out.println("Connection pool is full");
        }
    }
}
