package service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private DateUtil() {
    }

    
    public static String parse(String text, String fieldName) {
        try {
            return LocalDate.parse(text.trim()).toString();
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException(fieldName + " must be a real date written as yyyy-MM-dd (e.g. 2026-10-30).");
        }
    }
}
