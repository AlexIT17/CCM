package com.example.ccm.data;

public final class VtbDepositSettings {
    public final double minOnlineAmount;
    public final double minOfficeAmount;
    public final double minInterestBalance;
    public final int minTermMonths;
    public final int maxTermMonths;
    public final double maxUiAmount;
    public final double maxVkladRate;
    public final double maxAccountRate;
    public final long sourceId;
    public final long accountSourceId;

    public VtbDepositSettings(double minOnlineAmount, double minOfficeAmount,
                              double minInterestBalance, int minTermMonths,
                              int maxTermMonths, double maxUiAmount,
                              double maxVkladRate, double maxAccountRate, long sourceId,
                              long accountSourceId) {
        this.minOnlineAmount = minOnlineAmount;
        this.minOfficeAmount = minOfficeAmount;
        this.minInterestBalance = minInterestBalance;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.maxUiAmount = maxUiAmount;
        this.maxVkladRate = maxVkladRate;
        this.maxAccountRate = maxAccountRate;
        this.sourceId = sourceId;
        this.accountSourceId = accountSourceId;
    }
}
