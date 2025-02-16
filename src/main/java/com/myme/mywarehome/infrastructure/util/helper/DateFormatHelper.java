package com.myme.mywarehome.infrastructure.util.helper;

import com.myme.mywarehome.infrastructure.util.helper.exception.InvalidDateFormatException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateFormatHelper {
    public static String formatDate(LocalDate date) {
        try {
            if (date == null) {
                return null;
            } else {
                return date.toString();
            }
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }
    }

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }
    }
}
