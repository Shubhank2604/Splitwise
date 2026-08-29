package com.splitwise.domain;

import com.splitwise.exception.ConflictException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;

public final class Idempotency {
    private static final Pattern KEY_PATTERN = Pattern.compile("[A-Za-z0-9._:-]{8,100}");

    private Idempotency() {
    }

    public static String requireValidKey(String key) {
        if (key == null || !KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException(
                "Idempotency-Key must be 8-100 characters using letters, numbers, '.', '_', ':' or '-'"
            );
        }
        return key;
    }

    public static String fingerprint(String canonicalRequest) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(canonicalRequest.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    public static void requireSameRequest(String storedFingerprint, String fingerprint) {
        if (!storedFingerprint.equals(fingerprint)) {
            throw new ConflictException("Idempotency-Key was already used for a different request");
        }
    }
}
