package com.example.ccm.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MortgageLimits {
    private MortgageLimits() {}
    public static double maximumPrice(double maxLoan, double downPayment,
            double minDownPercent, double extraCosts, boolean withoutDownPayment) {
        if (!Double.isFinite(maxLoan) || !Double.isFinite(downPayment)
                || !Double.isFinite(minDownPercent) || !Double.isFinite(extraCosts)
                || maxLoan < 0 || downPayment < 0 || extraCosts < 0
                || minDownPercent < 0 || minDownPercent >= 100) return 0;
        BigDecimal loanBound = BigDecimal.valueOf(maxLoan).subtract(BigDecimal.valueOf(extraCosts))
                .add(BigDecimal.valueOf(withoutDownPayment ? 0 : downPayment));
        BigDecimal upper = loanBound;
        if (!withoutDownPayment && minDownPercent > 0) {
            BigDecimal downBound = BigDecimal.valueOf(downPayment).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(minDownPercent), 0, RoundingMode.FLOOR);
            upper = upper.min(downBound);
        }
        return upper.max(BigDecimal.ZERO).setScale(0, RoundingMode.FLOOR).doubleValue();
    }
}
