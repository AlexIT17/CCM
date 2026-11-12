package com.example.ccm;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class CalculatorUiTest {
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    private BankDetailActivity open(long bank, String type) {
        Intent intent = new Intent(instrumentation.getTargetContext(), BankDetailActivity.class)
                .putExtra("bankId", bank).putExtra("bankName", "Банк")
                .putExtra("type", type).putExtra("color", "#FFDD2D")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BankDetailActivity activity = (BankDetailActivity) instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
        return activity;
    }

    private String value(BankDetailActivity a, int id) {
        return ((EditText)a.findViewById(id)).getText().toString();
    }

    private void moveAmountToEnd(BankDetailActivity a, boolean maximum) {
        SeekBar slider = a.findViewById(R.id.seekAmount);
        slider.setProgress(maximum ? slider.getMax() - 1 : 1);
        int key = maximum ? android.view.KeyEvent.KEYCODE_DPAD_RIGHT : android.view.KeyEvent.KEYCODE_DPAD_LEFT;
        slider.onKeyDown(key, new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, key));
        assertEquals(maximum ? slider.getMax() : 0, slider.getProgress());
    }

    @Test public void allNineProductsOpenAndCalculate() {
        for (long bank = 1; bank <= 3; bank++) {
            for (String type : new String[]{"Кредит", "Вклад", "Ипотека"}) {
                BankDetailActivity a = open(bank, type);
                try {
                    instrumentation.runOnMainSync(() -> {
                        assertFalse("Initial rate: " + type, value(a, R.id.etRate).isEmpty());
                        assertFalse(((android.widget.TextView)a.findViewById(R.id.tvResult))
                                .getText().toString().contains("Проверьте"));
                        moveAmountToEnd(a, true);
                        assertFalse("Rate at maximum: " + type, value(a, R.id.etRate).isEmpty());
                        moveAmountToEnd(a, false);
                        assertFalse("Rate at minimum: " + type, value(a, R.id.etRate).isEmpty());
                    });
                } finally { instrumentation.runOnMainSync(a::finish); }
            }
        }
    }

    @Test public void downPaymentTypingPreservesPriceAndRestoresState() {
        BankDetailActivity a = open(3, "Ипотека");
        try {
            instrumentation.runOnMainSync(() -> {
                EditText down = a.findViewById(R.id.etDownPayment);
                String price = value(a, R.id.etAmount);
                down.requestFocus();
                down.setText("");
                down.setText("7");
                assertEquals(price, value(a, R.id.etAmount));
                down.setText("703500");
                down.clearFocus();
                moveAmountToEnd(a, true);
                assertEquals("3 500 000", value(a, R.id.etAmount));
                assertFalse(value(a, R.id.etRate).isEmpty());
                Bundle state = new Bundle();
                instrumentation.callActivityOnSaveInstanceState(a, state);
                down.setText("1000000");
                instrumentation.callActivityOnRestoreInstanceState(a, state);
                assertEquals("703 500", value(a, R.id.etDownPayment));
                assertEquals("3 500 000", value(a, R.id.etAmount));
            });
            instrumentation.waitForIdleSync();
            instrumentation.runOnMainSync(() -> assertFalse(value(a, R.id.etRate).isEmpty()));
        } finally { instrumentation.runOnMainSync(a::finish); }
    }

    @Test public void tbankTermDependentLimitsKeepRate() {
        for (String type : new String[]{"Кредит", "Вклад"}) {
            BankDetailActivity a = open(3, type);
            try {
                instrumentation.runOnMainSync(() -> {
                    ((EditText)a.findViewById(R.id.etTerm)).setText(type.equals("Кредит") ? "180" : "4");
                    moveAmountToEnd(a, true);
                    assertFalse("Maximum: " + type, value(a, R.id.etRate).isEmpty());
                    moveAmountToEnd(a, false);
                    assertFalse("Minimum: " + type, value(a, R.id.etRate).isEmpty());
                });
            } finally { instrumentation.runOnMainSync(a::finish); }
        }
    }

    @Test public void groupingDoesNotDeleteUnselectedDigits() {
        BankDetailActivity a = open(3, "Кредит");
        try {
            instrumentation.runOnMainSync(() -> {
                EditText amount = a.findViewById(R.id.etAmount);
                amount.requestFocus();
                amount.setText("12345");
                amount.setSelection(2, 3);
                amount.getText().delete(2, 3);
                assertEquals("12 345", amount.getText().toString());
                amount.setSelection(3);
                amount.getText().delete(2, 3);
                assertEquals("1 345", amount.getText().toString());
                amount.setText("12345");
                amount.setSelection(2);
                amount.getText().delete(2, 3);
                assertEquals("1 245", amount.getText().toString());
                amount.setText("123\u00a0456,78");
                assertEquals("123 456.78", amount.getText().toString());
            });
        } finally { instrumentation.runOnMainSync(a::finish); }
    }
}
