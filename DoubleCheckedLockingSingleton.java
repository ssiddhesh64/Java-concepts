/**
 * CONCEPT TAUGHT: Double-Checked Locking Singleton Pattern
 * 
 * WHY THIS WAS WRITTEN:
 * - Shows the thread-safe implementation of a lazy-initialized singleton.
 * 
 * KEY LESSONS:
 * - Use double check-then-act inside a synchronized block to prevent multiple instantiations.
 * - The 'volatile' keyword is mandatory to prevent compiler instruction reordering (partially initialized objects).
 */
public class DoubleCheckedLockingSingleton {
    private static volatile DoubleCheckedLockingSingleton instance;

    private DoubleCheckedLockingSingleton() {}

    public static DoubleCheckedLockingSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedLockingSingleton.class) {
                if (instance == null) {
                    instance = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return instance;
    }
}
