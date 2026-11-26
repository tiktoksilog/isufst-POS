package com.isufst.pos.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Small shared helpers for money, dates and password hashing.
 */
public final class Formatter {

    public static final String PESO = "₱";
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private Formatter() {
    }

    public static String peso(BigDecimal amount) {
        return PESO + MONEY.format(amount == null ? BigDecimal.ZERO : amount);
    }

    public static String money(BigDecimal amount) {
        return MONEY.format(amount == null ? BigDecimal.ZERO : amount);
    }

    public static String dateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_TIME);
    }

    /** SHA-256 hex hash; matches MySQL's SHA2(text, 256). */
    public static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
