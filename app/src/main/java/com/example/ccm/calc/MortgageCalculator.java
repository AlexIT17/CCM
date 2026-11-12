package com.example.ccm.calc;

public final class MortgageCalculator {
    private MortgageCalculator() {}

    public static CreditResult calculate(double amount, double annualRatePct, int months) {
        return CreditCalculator.calculate(amount, annualRatePct, months);
    }

    public static CreditResult calculateWithDownPayment(
            double propertyPrice, double downPayment,
            double annualRatePct, int months) {
        CalculationInput.validate(propertyPrice, annualRatePct, months);
        CalculationInput.finite(downPayment);
        if (downPayment < 0 || downPayment > propertyPrice) {
            throw new IllegalArgumentException("Взнос должен быть от нуля до стоимости объекта");
        }
        double principal = propertyPrice - downPayment;
        return CreditCalculator.calculate(principal, annualRatePct, months);
    }

    public static CreditResult calculateCombined(double principal, double subsidizedLimit,
            double subsidizedRate, double marketRate, int months) {
        CalculationInput.validate(principal, subsidizedRate, months);
        CalculationInput.validate(subsidizedLimit, marketRate, months);
        double first = Math.min(principal, subsidizedLimit);
        CreditResult a = calculate(first, subsidizedRate, months);
        CreditResult b = calculate(principal - first, marketRate, months);
        return new CreditResult(a.monthlyPayment + b.monthlyPayment,
                a.totalPayment + b.totalPayment, a.overpayment + b.overpayment);
    }
}
