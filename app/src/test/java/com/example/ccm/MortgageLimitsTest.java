package com.example.ccm;

import com.example.ccm.calc.MortgageLimits;
import org.junit.Test;
import static org.junit.Assert.*;

public class MortgageLimitsTest {
    @Test public void screenshotDownPaymentCapsPriceAtThreeAndHalfMillion() {
        assertEquals(3500000, MortgageLimits.maximumPrice(100000000,703500,20.1,0,false),0);
    }
    @Test public void loanLimitCanBeStricterThanDownPayment() {
        assertEquals(1500000, MortgageLimits.maximumPrice(1000000,500000,20.1,0,false),0);
    }
    @Test public void expensesConsumeLoanLimit() {
        assertEquals(1400000, MortgageLimits.maximumPrice(1000000,500000,20.1,100000,false),0);
    }
    @Test public void refinanceIgnoresDownPayment() {
        assertEquals(900000, MortgageLimits.maximumPrice(1000000,500000,0,100000,true),0);
    }
    @Test public void zeroDownHasNoPurchaseRange() {
        assertEquals(0, MortgageLimits.maximumPrice(100000000,0,20.1,0,false),0);
    }
    @Test public void maximumSatisfiesLoanAndDownPaymentConstraints() {
        for(double pct:new double[]{5,20.1,30.1,40.1}) {
            double down=1053698, extra=25000, limit=6000000;
            double price=MortgageLimits.maximumPrice(limit,down,pct,extra,false);
            assertTrue(price-down+extra <= limit);
            assertTrue(down+0.000001 >= price*pct/100);
            assertTrue(price+1-down+extra>limit || down<(price+1)*pct/100);
        }
    }
}
