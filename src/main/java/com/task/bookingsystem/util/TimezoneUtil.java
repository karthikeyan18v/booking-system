package com.task.bookingsystem.util;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimezoneUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static ZoneId validateAndGet(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone);
        }
    }

    public static Instant toUtc(String localDateTimeStr, ZoneId zone) {
        LocalDateTime ldt = LocalDateTime.parse(localDateTimeStr, FORMATTER);
        return ldt.atZone(zone).toInstant();
    }

    public static String toLocalString(Instant utcInstant, ZoneId zone) {
        return utcInstant.atZone(zone).format(FORMATTER);
    }
}
