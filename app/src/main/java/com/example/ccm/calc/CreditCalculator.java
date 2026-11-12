package com.example.ccm.calc;

public final class CreditCalculator {
    private CreditCalculator() {}


    public static CreditResult calculate(double amount, double annualRatePct, int months) {
        CalculationInput.validate(amount, annualRatePct, months);
        double r = annualRatePct / 100.0 / 12.0;
        double monthly;
        if (r == 0) {
            monthly = amount / months;
        } else {
            monthly = amount * r / -Math.expm1(-months * Math.log1p(r));
        }
        double total = monthly * months;
        CalculationInput.finite(total);
        return new CreditResult(monthly, total, Math.max(0, total - amount));
    }
}
