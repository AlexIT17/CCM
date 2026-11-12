package com.example.ccm;

import com.example.ccm.calc.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class FinancialCalculatorsTest {
    @Test public void tbankMortgageAgreesWithScreenshotWithinOneRuble() {
        CreditResult result = MortgageCalculator.calculateWithDownPayment(3500000,703500,17.9,360);
        assertEquals(2796500, result.totalPayment-result.overpayment, 0.000001);
        assertEquals(41918, result.monthlyPayment, 1.0);
    }
    @Test public void annuityReference() {
        CreditResult r = CreditCalculator.calculate(100000, 12, 12);
        assertEquals(8884.87886783416, r.monthlyPayment, 0.000001);
        assertEquals(106618.5464140099, r.totalPayment, 0.000001);
    }
    @Test public void zeroInterest() {
        CreditResult r = CreditCalculator.calculate(120000, 0, 12);
        assertEquals(10000, r.monthlyPayment, 0);
        assertEquals(0, r.overpayment, 0);
    }
    @Test public void nearZeroRateRemainsStable() {
        assertEquals(10000, CreditCalculator.calculate(120000, 1e-12, 12).monthlyPayment, 1e-6);
    }
    @Test public void zeroPrincipal() {
        assertEquals(0, CreditCalculator.calculate(0, 20, 12).totalPayment, 0);
    }
    @Test public void annuityPaysOffDebt() {
        double balance = 5000000;
        CreditResult r = CreditCalculator.calculate(balance, 16.9, 360);
        for (int m=0; m<360; m++) balance = balance * (1 + 16.9/1200) - r.monthlyPayment;
        assertEquals(0, balance, 0.001);
    }
    @Test public void mortgageSubtractsDownPayment() {
        assertEquals(CreditCalculator.calculate(4000000, 12, 240).monthlyPayment,
                MortgageCalculator.calculateWithDownPayment(5000000, 1000000, 12, 240).monthlyPayment, 0);
    }
    @Test public void fullyPaidProperty() {
        assertEquals(0, MortgageCalculator.calculateWithDownPayment(5000000,5000000,12,240).totalPayment,0);
    }
    @Test public void combinedPaymentIsSumNotAverageRate() {
        CreditResult r = MortgageCalculator.calculateCombined(10000000,6000000,6,18,240);
        assertEquals(CreditCalculator.calculate(6000000,6,240).monthlyPayment
                + CreditCalculator.calculate(4000000,18,240).monthlyPayment, r.monthlyPayment, 1e-8);
        assertTrue(r.monthlyPayment > CreditCalculator.calculate(10000000,10.8,240).monthlyPayment);
    }
    @Test public void combinedBelowLimit() {
        assertEquals(CreditCalculator.calculate(5000000,6,240).totalPayment,
                MortgageCalculator.calculateCombined(5000000,6000000,6,18,240).totalPayment, 0);
    }
    @Test public void simpleDeposit() {
        DepositResult r=DepositCalculator.calculate(100000,12,12,"END");
        assertEquals(112000,r.finalAmount,1e-8);
        assertEquals(12000,r.interest,1e-8);
    }
    @Test public void externalPayoutDoesNotCompound() {
        assertEquals(12000,DepositCalculator.calculate(100000,12,12,"MONTHLY_ACCOUNT").interest,1e-8);
    }
    @Test public void monthlyCapitalization() {
        assertEquals(112682.50301319698,
                DepositCalculator.calculate(100000,12,12,"MONTHLY_CAPITALIZATION").finalAmount,1e-7);
    }
    @Test public void savingsCapIncludesCapitalizedInterest() {

        assertEquals(1020.05, SavingsCalculator.calculate(1000,2,6,12,12,0,1000).finalAmount,1e-8);
    }
    @Test public void promoEndsAfterTwoMonths() {
        assertEquals(1020.1, SavingsCalculator.calculate(1000,3,0,0,12,2,100000).finalAmount,1e-8);
    }
    @Test public void zeroRateDeposit() {
        assertEquals(100000,DepositCalculator.calculate(100000,0,12,"END").finalAmount,0);
    }
    @Test(expected=IllegalArgumentException.class) public void rejectNaN() { CreditCalculator.calculate(Double.NaN,12,12); }
    @Test(expected=IllegalArgumentException.class) public void rejectInfinity() { DepositCalculator.calculate(1000,Double.POSITIVE_INFINITY,12); }
    @Test(expected=IllegalArgumentException.class) public void rejectNegativeRate() { CreditCalculator.calculate(1000,-1,12); }
    @Test(expected=IllegalArgumentException.class) public void rejectNegativePrincipal() { CreditCalculator.calculate(-1000,12,12); }
    @Test(expected=IllegalArgumentException.class) public void rejectZeroTerm() { CreditCalculator.calculate(1000,12,0); }
    @Test(expected=IllegalArgumentException.class) public void rejectExcessiveTerm() { DepositCalculator.calculate(1000,12,1201); }
    @Test(expected=IllegalArgumentException.class) public void rejectInvalidPayout() { DepositCalculator.calculate(1000,12,12,"UNKNOWN"); }
    @Test(expected=IllegalArgumentException.class) public void rejectExcessiveDownPayment() { MortgageCalculator.calculateWithDownPayment(1000,1001,12,12); }
    @Test(expected=IllegalArgumentException.class) public void rejectNegativeDownPayment() { MortgageCalculator.calculateWithDownPayment(1000,-1,12,12); }
    @Test(expected=IllegalArgumentException.class) public void rejectInvalidSavingsLimit() { SavingsCalculator.calculate(1000,12,6,12,14,2,-1); }
}
