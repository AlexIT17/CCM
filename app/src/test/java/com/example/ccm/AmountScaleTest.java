package com.example.ccm;

import com.example.ccm.calc.AmountScale;
import org.junit.Test;
import static org.junit.Assert.*;

public class AmountScaleTest {
    @Test public void vtbMortgageMaximumDoesNotExceedInputFilter() {
        double maximum=100000000.0/(1-20.1/100.0);
        long amount=AmountScale.wholeRubles(maximum,500000,maximum);
        assertEquals(125156445L,amount);
        assertTrue(amount<=maximum);
        assertTrue(Math.round(maximum)>maximum); // Воспроизводит причину очистки поля.
    }
    @Test public void interpolationErrorAtMaximumIsClamped() {
        assertEquals(100000000L,AmountScale.wholeRubles(100000000.0000002,500000,100000000));
    }
    @Test public void endpointsStayInRangeForEveryMortgageDownPayment() {
        for(double percent : new double[]{0,5,20.1,30.1,40.1}) {
            double maximum=10000000/(1-percent/100);
            long amount=AmountScale.wholeRubles(maximum,500000,maximum);
            assertTrue(amount<=maximum);
            assertEquals(500000L,AmountScale.wholeRubles(499999.999999,500000,maximum));
        }
    }
}
