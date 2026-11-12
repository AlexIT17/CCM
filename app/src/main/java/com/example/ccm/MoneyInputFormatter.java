package com.example.ccm;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;
import com.example.ccm.calc.MoneyText;

final class MoneyInputFormatter implements TextWatcher {
    private final EditText field;
    private boolean changing;
    private String before = "";
    private int deletionStart = -1;
    private int selectionBeforeStart, selectionBeforeEnd;
    private boolean backwardDeletion;

    static void attach(EditText field) {
        field.setKeyListener(DigitsKeyListener.getInstance("0123456789., \u00a0\u202f"));
        field.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        MoneyInputFormatter formatter = new MoneyInputFormatter(field);
        field.addTextChangedListener(formatter);
        formatter.afterTextChanged(field.getText());
    }

    private MoneyInputFormatter(EditText field) { this.field = field; }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (!changing) {
            before = s.toString();
            selectionBeforeStart = field.getSelectionStart();
            selectionBeforeEnd = field.getSelectionEnd();
        }
    }

    @Override public void onTextChanged(CharSequence s, int start, int beforeCount, int count) {
        if (changing) return;
        deletionStart = beforeCount == 1 && count == 0 && start > 0
                && selectionBeforeStart == selectionBeforeEnd
                && (selectionBeforeStart == start || selectionBeforeStart == start + 1)
                && start < before.length() && before.charAt(start) == ' ' ? start : -1;
        backwardDeletion = selectionBeforeStart == start + 1;
    }

    @Override public void afterTextChanged(Editable editable) {
        if (changing) return;
        changing = true;
        try {
            int cursor = Math.max(0, field.getSelectionStart());
            if (deletionStart > 0 && deletionStart <= editable.length()) {
                int digit = backwardDeletion ? deletionStart - 1 : deletionStart;
                if (digit < editable.length()) editable.delete(digit, digit + 1);
                cursor = digit;
            }
            String value = editable.toString();
            int logicalCursor = MoneyText.ungroup(value.substring(0, Math.min(cursor, value.length()))).length();
            String formatted = MoneyText.input(value);
            if (!formatted.equals(value)) {
                editable.replace(0, editable.length(), formatted);
                String actual = editable.toString();
                int position = 0, seen = 0;
                while (position < actual.length() && seen < logicalCursor) {
                    if (actual.charAt(position) != ' ') seen++;
                    position++;
                }
                field.setSelection(position);
            }
        } finally {
            deletionStart = -1;
            changing = false;
        }
    }
}
