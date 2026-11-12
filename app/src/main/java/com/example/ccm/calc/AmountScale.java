package com.example.ccm.calc;

public final class AmountScale {
    private AmountScale() {}

    public static long wholeRubles(double value, double minimum, double maximum) {
        if (!Double.isFinite(value) || !Double.isFinite(minimum) || !Double.isFinite(maximum)
                || minimum < 0 || Math.ceil(minimum) > Math.floor(maximum)
                || maximum >= Long.MAX_VALUE) {
            throw new IllegalArgumentException("Недопустимые границы шкалы суммы");
        }
        return (long) Math.min(Math.floor(maximum), Math.max(Math.ceil(minimum), Math.round(value)));
    }
}
