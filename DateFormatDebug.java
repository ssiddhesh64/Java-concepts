/**
 * CONCEPT TAUGHT: SimpleDateFormat Thread Safety
 * 
 * WHY THIS WAS WRITTEN:
 * - Exposes the concurrency bug in SimpleDateFormat and provides modern thread-safe alternatives.
 * 
 * KEY LESSONS:
 * - SimpleDateFormat is NOT thread-safe and will corrupt dates if shared across threads.
 * - Use Java 8 DateTimeFormatter (which is immutable and thread-safe) or ThreadLocal wrappers.
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

public class DateFormatDebug {
    // private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        Runnable task = () -> {
            try {
                String dateStr = "2026-06-23";
                // Date parsedDate = sdf.parse(dateStr);
                // String formattedStr = sdf.format(parsedDate);
                LocalDate date = LocalDate.parse(dateStr, FORMATTER);
                String formattedDate = date.format(FORMATTER);
                
                if (!dateStr.equals(formattedDate)) {
                    System.out.println("Mismatch: Expected " + dateStr + " but got " + formattedDate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 100; i++) {
            executor.submit(task);
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
