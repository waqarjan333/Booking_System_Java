package com.bpc.util;

import java.time.LocalDateTime;

public class DateTimeUtil {
    public static LocalDateTime createDateTime(int year, int month, int day, int hour) {
        return LocalDateTime.of(year, month, day, hour, 0);
    }
}