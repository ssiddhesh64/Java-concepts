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
