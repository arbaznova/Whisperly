package multithreadedserver.observability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger() {}

    private static void log(String level, String component, String message) {
        String time = LocalDateTime.now().format(FORMATTER);
        System.out.printf(
                "%s | %-5s | %-15s | %s%n",
                time, level, component, message
        );
    }

    public static void info(String component, String message) {
        log("INFO", component, message);
    }

    public static void warn(String component, String message) {
        log("WARN", component, message);
    }

    public static void error(String component, String message) {
        log("ERROR", component, message);
    }
}