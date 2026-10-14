package com.example.ccm.data;

import android.graphics.Color;

public class Bank {
    public final long id;
    public final String name;
    public final String colorHex;   // "#21A038"
    public final String website;

    public Bank(long id, String name, String colorHex, String website) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
        this.website = website;
    }

    public int color() { return Color.parseColor(colorHex); }
}