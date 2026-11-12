package com.example.ccm;

import com.example.ccm.calc.MoneyText;
import org.junit.Test;
import static org.junit.Assert.*;

public class MoneyTextTest {
    @Test public void paymentAndMillionHaveGroupsAndKopecks() {
        assertEquals("10 081.66", MoneyText.output(10081.66));
        assertEquals("1 000 000.00", MoneyText.output(1000000));
        assertEquals("0.00", MoneyText.output(0));
    }
    @Test public void unfinishedDecimalInputIsPreserved() {
        assertEquals("", MoneyText.input(""));
        assertEquals("1 234.", MoneyText.input("1234."));
        assertEquals("1 234.0", MoneyText.input("1234,0"));
        assertEquals("1 234.00", MoneyText.input("1234.00"));
    }
    @Test public void pastedSpacesAndCommaAreNormalized() {
        assertEquals("1 234 567.89", MoneyText.input("1\u00a0234\u202f567,89"));
    }
    @Test public void formattingDoesNotChangeCalculationValue() {
        for (String raw : new String[]{"0", "999.99", "1000", "10081.66", "125156445", "2863989.96"}) {
            assertEquals(Double.parseDouble(raw), Double.parseDouble(MoneyText.ungroup(MoneyText.input(raw))), 0);
        }
    }
    @Test public void regroupingIsIdempotentWithoutRoundingInput() {
        assertEquals("1 234 567.8901", MoneyText.input(MoneyText.input("1234567.8901")));
    }
}
