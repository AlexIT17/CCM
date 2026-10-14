package com.example.ccm.calc;

public final class MortgageCalculator {
    private MortgageCalculator() {}

    public static CreditResult calculate(double amount, double annualRatePct, int months) {
        return CreditCalculator.calculate(amount, annualRatePct, months);
    }

    public static CreditResult calculateWithDownPayment(
            double propertyPrice, double downPayment,
            double annualRatePct, int months) {
        double principal = Math.max(0, propertyPrice - downPayment);
        return CreditCalculator.calculate(principal, annualRatePct, months);
    }
}