package com.example.ccm.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    private final DatabaseHelper helper;

    public Repository(Context c) { this.helper = DatabaseHelper.get(c); }

    public List<Bank> getBanksForType(String type) {
        String sql = "SELECT DISTINCT b.id, b.name, b.color, b.website " +
                "FROM banks b JOIN products p ON p.bank_id = b.id " +
                "WHERE p.type = ? ORDER BY b.name";
        List<Bank> out = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, new String[]{type})) {
            while (c.moveToNext()) {
                out.add(new Bank(c.getLong(0), c.getString(1), c.getString(2), c.getString(3)));
            }
        }
        return out;
    }

    public Product getProduct(long bankId, String type) {
        String sql = "SELECT id, bank_id, type, name FROM products " +
                "WHERE bank_id = ? AND type = ? LIMIT 1";
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(bankId), type})) {
            if (c.moveToFirst()) {
                return new Product(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3));
            }
        }
        return null;
    }

    public List<RateRule> getRateRules(long productId) {
        String sql = "SELECT id, product_id, rate, min_amount, max_amount, " +
                "min_term_months, max_term_months, source_id " +
                "FROM rate_rules WHERE product_id = ?";
        List<RateRule> out = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(productId)})) {
            while (c.moveToNext()) {
                out.add(new RateRule(c.getLong(0), c.getLong(1), c.getDouble(2),
                        c.getDouble(3), c.getDouble(4),
                        c.getInt(5), c.getInt(6), c.getLong(7)));
            }
        }
        return out;
    }

    /** Ставка, подходящая по сумме и сроку. Если не нашлось — первая из правил. */
    public RateRule findRate(long productId, double amount, int months) {
        List<RateRule> all = getRateRules(productId);
        for (RateRule r : all) if (r.matches(amount, months)) return r;
        return all.isEmpty() ? null : all.get(0);
    }

    /** Правило, подходящее под сумму и срок. null, если ни одно не подходит. */
    public RateRule findMatchingRule(long productId, double amount, int months) {
        for (RateRule r : getRateRules(productId)) {
            if (r.matches(amount, months)) return r;
        }
        return null;
    }

    public SourceInfo getSource(long sourceId) {
        String sql = "SELECT id, url, checked_date FROM source_info WHERE id = ?";
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(sourceId)})) {
            if (c.moveToFirst()) {
                return new SourceInfo(c.getLong(0), c.getString(1), c.getString(2));
            }
        }
        return null;
    }
}