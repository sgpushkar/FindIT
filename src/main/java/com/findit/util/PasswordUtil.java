package com.findit.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * All password handling goes through this class, so the hashing strategy
 * can be upgraded later (e.g. to BCrypt) without touching DAOs or servlets.
 * Currently uses SHA-256 - fine for a college demo, not for production.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean verify(String plainText, String storedHash) {
        if (plainText == null || storedHash == null) {
            return false;
        }
        return MessageDigest.isEqual(
                hash(plainText).getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8));
    }
}
