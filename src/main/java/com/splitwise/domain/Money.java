package com.splitwise.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    private Money() {
    }

    public static BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        return value.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static BigDecimal positive(BigDecimal value) {
        BigDecimal normalized = normalize(value);
        if (normalized.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return normalized;
    }
}
