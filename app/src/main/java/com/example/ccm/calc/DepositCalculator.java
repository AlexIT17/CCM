package com.example.ccm.calc;

public final class DepositCalculator {
    private DepositCalculator() {}

    public static DepositResult calculate(double amount, double annualRatePct, int months) {
        if (amount <= 0 || months <= 0 || annualRatePct < 0) {
            return new DepositResult(amount, 0);
        }
        double r = annualRatePct / 100.0 / 12.0;
        double finalAmount = amount * Math.pow(1 + r, months);
        return new DepositResult(finalAmount, finalAmount - amount);
    }
}