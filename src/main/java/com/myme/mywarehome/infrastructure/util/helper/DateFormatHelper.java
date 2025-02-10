package com.myme.mywarehome.infrastructure.util.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatHelper {
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
}
