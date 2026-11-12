package com.example.ccm.calc;

public final class SavingsCalculator {
    private SavingsCalculator() {}
    public static DepositResult calculate(double amount, int months, double baseRate,
            double regularRate, double promoRate, int promoMonths, double enhancedLimit) {
        CalculationInput.validate(amount, baseRate, months);
        CalculationInput.validate(amount, regularRate, months);
        CalculationInput.validate(amount, promoRate, months);
        CalculationInput.finite(enhancedLimit);
        if (enhancedLimit < 0 || promoMonths < 0) throw new IllegalArgumentException("Неверный лимит");
        double balance = amount;
        for (int month = 0; month < months; month++) {
            double eligible = Math.min(balance, enhancedLimit);
            double rate = month < promoMonths ? promoRate : regularRate;
            balance += (eligible * rate + (balance - eligible) * baseRate) / 1200.0;
        }
        CalculationInput.finite(balance);
        return new DepositResult(balance, balance - amount);
    }
}
