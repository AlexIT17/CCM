package com.example.ccm.calc;

public final class DepositResult {
    public final double finalAmount;
    public final double interest;
    public DepositResult(double finalAmount, double interest) {
        this.finalAmount = finalAmount;
        this.interest = interest;
    }
}