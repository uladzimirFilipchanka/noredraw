package creator.export.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class Retryable {

    public static <R> R withRetries(Supplier<R> supplier, int maxRetries, long delay) {
        int attempt = 0;

        while (attempt < maxRetries) {
            attempt++;
            try {
                // Attempt the operation and return if successful
                return supplier.get();
            } catch (Exception e) {
                log.error("Attempt " + attempt + " failed: " + e.getMessage(), e);
                if (attempt < maxRetries) {
                    log.info("Retrying in " + delay + "ms");
                    sleepUninterruptedly(delay);
                } else {
                    throw new RuntimeException("Max retries reached, unable to complete the operation.");
                }
            }
        }
        return null; // Should never reach this line
    }

    @SneakyThrows
    private static void sleepUninterruptedly(long delay) {
        TimeUnit.MILLISECONDS.sleep(delay);
    }
}