package com.example.ccm.calc;

public final class CreditCalculator {
    private CreditCalculator() {}

    /**
     * @param amount        сумма кредита, ₽
     * @param annualRatePct ставка, % годовых
     * @param months        срок, месяцев
     */
    public static CreditResult calculate(double amount, double annualRatePct, int months) {
        if (amount <= 0 || months <= 0 || annualRatePct < 0) {
            return new CreditResult(0, 0, 0);
        }
        double r = annualRatePct / 100.0 / 12.0;
        double monthly;
        if (r == 0) {
            monthly = amount / months;
        } else {
            double pow = Math.pow(1 + r, months);
            monthly = amount * r * pow / (pow - 1);
        }
        double total = monthly * months;
        return new CreditResult(monthly, total, total - amount);
    }
}