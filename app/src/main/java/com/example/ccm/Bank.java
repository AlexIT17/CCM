package com.example.ccm;

import java.util.Arrays;
import java.util.List;

public class Bank {
    public final String name;
    public final int colorRes;

    public Bank(String name, int colorRes) {
        this.name = name;
        this.colorRes = colorRes;
    }

    public static final List<Bank> BANKS = Arrays.asList(
            new Bank("Сбербанк", R.color.sber),
            new Bank("ВТБ", R.color.vtb),
            new Bank("Т-Банк", R.color.tbank),
            new Bank("Альфа-Банк", R.color.alfa),
            new Bank("Газпромбанк", R.color.gazprom)
    );
}