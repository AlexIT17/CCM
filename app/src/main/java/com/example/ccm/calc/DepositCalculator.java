package com.example.ccm.calc;

public final class DepositCalculator {
    private DepositCalculator() {}

    public static DepositResult calculate(double amount, double annualRatePct, int months, String payoutMode) {
        CalculationInput.validate(amount, annualRatePct, months);
        if (!"END".equals(payoutMode) && !"MONTHLY_ACCOUNT".equals(payoutMode)
                && !"MONTHLY_CAPITALIZATION".equals(payoutMode)) {
            throw new IllegalArgumentException("Неизвестный способ выплаты процентов");
        }

        double monthlyRate = annualRatePct / 100.0 / 12.0;
        double interest;
        double finalAmount;
        if ("MONTHLY_CAPITALIZATION".equals(payoutMode)) {
            interest = amount * Math.expm1(months * Math.log1p(monthlyRate));
            finalAmount = amount + interest;
        } else {
            interest = amount * (annualRatePct / 100.0) * (months / 12.0);
            finalAmount = amount + interest;
        }
        CalculationInput.finite(finalAmount);
        return new DepositResult(finalAmount, interest);
    }

    public static DepositResult calculate(double amount, double annualRatePct, int months) {
        return calculate(amount, annualRatePct, months, "MONTHLY_CAPITALIZATION");
    }
}
