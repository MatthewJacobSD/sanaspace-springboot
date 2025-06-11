package io.github.matthewjacobsd.sanaspace.utils;

import java.util.HashMap;
import java.util.Map;

public class PhoneUtils {

    private static final LoggerUtil log = new LoggerUtil(PhoneUtils.class);

    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;

        String digits = phoneNumber.replaceAll("[^0-9]", "");

        if (digits.length() >= 10) {
            digits = digits.substring(0, 10);
            return String.format("%s-%s-%s", 
                digits.substring(0, 3), 
                digits.substring(3, 6), 
                digits.substring(6, 10));
        } else {
            Map<String, Object> meta = new HashMap<>();
            meta.put("rawInput", phoneNumber);
            meta.put("digits", digits);
            log.warn("Phone number too short for formatting: " + meta);
            return phoneNumber; // fallback to original
        }
    }
}
