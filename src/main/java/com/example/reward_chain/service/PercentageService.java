package com.example.reward_chain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PercentageService {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    private PercentageService() {}

    /** Convert a 0..100 human-friendly percent to 0..1 fraction. */
    public static BigDecimal toFraction(BigDecimal percent0to100) {
        if (percent0to100 == null) return BigDecimal.ZERO;
        return percent0to100.divide(ONE_HUNDRED, 8, RoundingMode.HALF_UP);
    }

    /** Convert a 0..1 fraction to 0..100 human-friendly percent. */
    public static BigDecimal toPercent(BigDecimal fraction0to1) {
        if (fraction0to1 == null) return BigDecimal.ZERO;
        return fraction0to1.multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }
}
