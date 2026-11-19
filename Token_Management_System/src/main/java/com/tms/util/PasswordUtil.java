package com.tms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Hashes passwords with SHA-256 so the table never stores plain text.
 */
public class PasswordUtil {

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("could not hash password", e);
        }
    }

    public static boolean matches(String plain, String storedHash) {
        return hash(plain).equals(storedHash);
    }
}
