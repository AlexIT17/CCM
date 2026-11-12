package com.example.ccm.calc;

import java.util.Locale;

public final class MoneyText {
    private MoneyText() {}

    public static String ungroup(String value) {
        return value.replace(" ", "").replace("\u00a0", "").replace("\u202f", "");
    }

    public static String input(String value) {
        String raw = ungroup(value).replace(',', '.');
        if (!raw.matches("[0-9]*(\\.[0-9]*)?")) return value;
        int dot = raw.indexOf('.');
        String integer = dot < 0 ? raw : raw.substring(0, dot);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < integer.length(); i++) {
            if (i > 0 && (integer.length() - i) % 3 == 0) out.append(' ');
            out.append(integer.charAt(i));
        }
        if (dot >= 0) out.append(raw.substring(dot));
        return out.toString();
    }

    public static String output(double value) {
        return String.format(Locale.US, "%,.2f", value).replace(',', ' ');
    }
}
