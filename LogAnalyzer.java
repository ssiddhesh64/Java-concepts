import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LogAnalyzer {

    public long countErrorLogs(Path logFilePath) throws IOException {
        // Wrap the stream in try-with-resources to guarantee the file descriptor is closed
        try (Stream<String> lines = Files.lines(logFilePath)) {
            return lines.filter(line -> line.contains("ERROR"))
                    .count();
        }
    }
}
