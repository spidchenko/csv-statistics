package ua.pp.spidchenko.importcsv.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    static final String DATETIME_PATTERN = "yyyy/MM/dd HH:mm";
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN); // 2023/08/25 12:46

    public static LocalDateTime getHour(String dateTime) {
        LocalDateTime creationDateTime = LocalDateTime.parse(dateTime, formatter);
        return creationDateTime.withMinute(0).withSecond(0);
    }

    public static LocalDateTime getDay(String dateTime) {
        LocalDateTime creationDateTime = LocalDateTime.parse(dateTime, formatter);
        return creationDateTime.withHour(0).withMinute(0).withSecond(0);
    }
}
