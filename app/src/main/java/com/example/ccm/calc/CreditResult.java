package com.example.ccm.calc;

public final class CreditResult {
    public final double monthlyPayment;
    public final double totalPayment;
    public final double overpayment;

    public CreditResult(double monthlyPayment, double totalPayment, double overpayment) {
        this.monthlyPayment = monthlyPayment;
        this.totalPayment = totalPayment;
        this.overpayment = overpayment;
    }
}