package com.example.ccm.data;

public class MortgageRateRule {
    public final double ratePercent;
    public final double minRatePercent;
    public final double maxLoan;
    public final double minDownPaymentPercent;
    public final double minPropertyPrice;
    public final int minTermMonths;
    public final int maxTermMonths;

    public MortgageRateRule(double ratePercent, double minRatePercent, double maxLoan,
                            double minDownPaymentPercent, double minPropertyPrice,
                            int minTermMonths, int maxTermMonths) {
        this.ratePercent = ratePercent;
        this.minRatePercent = minRatePercent;
        this.maxLoan = maxLoan;
        this.minDownPaymentPercent = minDownPaymentPercent;
        this.minPropertyPrice = minPropertyPrice;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
    }
}
