package com.findit.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/** Small helper for form validation and shared constants. */
public final class Validator {

    public static final List<String> CATEGORIES = Collections.unmodifiableList(Arrays.asList(
            "Electronics", "Documents", "Wallet", "Keys", "Clothing",
            "Accessories", "Books", "ID Cards", "Other"));

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validator() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String clean(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean isEmail(String s) {
        return s != null && s.length() <= 255 && EMAIL.matcher(s).matches();
    }

    public static boolean isType(String s) {
        return "LOST".equals(s) || "FOUND".equals(s);
    }

    public static boolean isStatus(String s) {
        return "ACTIVE".equals(s) || "RESOLVED".equals(s);
    }

    public static boolean isCategory(String s) {
        return CATEGORIES.contains(s);
    }

    /** Accepts yyyy-MM-dd dates that are not in the future. */
    public static boolean isValidPastDate(String s) {
        try {
            return !LocalDate.parse(s).isAfter(LocalDate.now());
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }
}
