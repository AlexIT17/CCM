package com.example.ccm.calc;

final class CalculationInput {
    private CalculationInput() {}
    static void validate(double amount, double rate, int months) {
        finite(amount);
        finite(rate);
        if (amount < 0 || rate < 0 || rate > 1000 || months < 1 || months > 1200) {
            throw new IllegalArgumentException("Некорректная сумма, ставка или срок");
        }
    }
    static void finite(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Значение должно быть конечным числом");
        }
    }
}
