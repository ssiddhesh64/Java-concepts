import java.util.concurrent.atomic.AtomicIntegerArray;

public class ServerStatusManager {
    // The developer made the array volatile to guarantee visibility
    // private volatile boolean[] serverStatus = new boolean[10];

    // public void setStatus(int serverId, boolean status) {
    // if (serverId >= 0 && serverId < serverStatus.length) {
    // serverStatus[serverId] = status; // Writes here are not visible immediately
    // }
    // }

    // public boolean getStatus(int serverId) {
    // if (serverId >= 0 && serverId < serverStatus.length) {
    // return serverStatus[serverId]; // Reads here may return stale data
    // }
    // return false;
    // }

    private AtomicIntegerArray serverStatus = new AtomicIntegerArray(10);

    public void setStatus(int serverId, boolean status) {
        if (serverId >= 0 && serverId < serverStatus.length()) {
            serverStatus.set(serverId, status ? 1 : 0);
        }
    }

    public boolean getStatus(int serverId) {
        if (serverId >= 0 && serverId < serverStatus.length()) {
            return serverStatus.get(serverId) == 1;
        }
        return false;
    }
}
