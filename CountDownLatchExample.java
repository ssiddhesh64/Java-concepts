import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.HashSet;
import java.util.Set;

class ServiceInitializer {

    private final CountDownLatch startupLatch = new CountDownLatch(3);
    private final Set<String> services = ConcurrentHashMap.newKeySet();

    public void completeTask(String taskName) {
        if (services.add(taskName)) {
            startupLatch.countDown();
        }
    }

    public void awaitStartUp() throws InterruptedException {
        startupLatch.await();
        System.out.println("All initializations completed. Service is ready...");
    }
}

public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        ServiceInitializer serviceInitializer = new ServiceInitializer();

        new Thread(() -> {
            serviceInitializer.completeTask("database");
        }).start();

        new Thread(() -> {
            serviceInitializer.completeTask("cache");
        }).start();

        new Thread(() -> {
            serviceInitializer.completeTask("queue");
        }).start();

        serviceInitializer.awaitStartUp();

        System.out.println("Now accepting requests");
    }
}
