package com.example.ccm.data;

public class RateRule {
    public final long id;
    public final long productId;
    public final double ratePercent;
    public final double minAmount;
    public final double maxAmount;
    public final int minTermMonths;
    public final int maxTermMonths;
    public final long sourceId;

    public RateRule(long id, long productId, double ratePercent,
                    double minAmount, double maxAmount,
                    int minTermMonths, int maxTermMonths, long sourceId) {
        this.id = id;
        this.productId = productId;
        this.ratePercent = ratePercent;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.sourceId = sourceId;
    }

    public boolean matches(double amount, int months) {
        return amount >= minAmount && amount <= maxAmount
                && months >= minTermMonths && months <= maxTermMonths;
    }
}