package dev.lokspel.practice.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TimeUtil {

    private TimeUtil() {}

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static String formatDate(long millis) {
        return DATE_TIME_FORMAT.format(
                Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
        );
    }

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;

        seconds %= 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}