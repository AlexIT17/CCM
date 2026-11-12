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
        String sql = "SELECT id, product_id, rate, client_segment, min_amount, max_amount, " +
                "min_term_months, max_term_months, source_id " +
                "FROM rate_rules WHERE product_id = ? ORDER BY rate ASC, id ASC";
        List<RateRule> out = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(productId)})) {
            while (c.moveToNext()) {
                out.add(new RateRule(c.getLong(0), c.getLong(1), c.getDouble(2),
                        c.getString(3), c.getDouble(4), c.getDouble(5),
                        c.getInt(6), c.getInt(7), c.getLong(8)));
            }
        }
        return out;
    }

    public RateRule findRate(long productId, double amount, int months) {
        return findMatchingRule(productId, amount, months, "ALL");
    }

    public RateRule findMatchingRule(long productId, double amount, int months) {
        return findMatchingRule(productId, amount, months, "ALL");
    }

    public RateRule findMatchingRule(long productId, double amount, int months,
                                     String clientSegment) {
        List<RateRule> rules = getRateRules(productId);
        String requestedSegment = clientSegment == null ? "ALL" : clientSegment;

        for (RateRule r : rules) {
            if (r.matches(amount, months) && requestedSegment.equals(r.clientSegment)) {
                return r;
            }
        }
        for (RateRule r : rules) {
            if (r.matches(amount, months) && "ALL".equals(r.clientSegment)) {
                return r;
            }
        }
        return null;
    }

    public double findSberDepositBaseRate(long productId, double amount, int months, boolean topUp) {
        String sql = "SELECT rate FROM deposit_rate_rules " +
                "WHERE product_id = ? AND min_amount <= ? AND max_amount >= ? " +
                "AND min_term_months <= ? AND max_term_months >= ? AND top_up = ? " +
                "ORDER BY min_amount DESC LIMIT 1";
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, new String[]{
                String.valueOf(productId), String.valueOf(amount), String.valueOf(amount),
                String.valueOf(months), String.valueOf(months), topUp ? "1" : "0"})) {
            if (c.moveToFirst()) return c.getDouble(0);
        }
        return -1;
    }

    public VtbDepositSettings getVtbDepositSettings(long productId) {
        String sql = "SELECT min_online_amount, min_office_amount, min_interest_balance, " +
                "min_term_months, max_term_months, max_ui_amount, max_vklad_rate, " +
                "max_account_rate, source_id, account_source_id FROM vtb_deposit_settings WHERE product_id = ?";
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql,
                new String[]{String.valueOf(productId)})) {
            if (c.moveToFirst()) {
                return new VtbDepositSettings(c.getDouble(0), c.getDouble(1), c.getDouble(2),
                        c.getInt(3), c.getInt(4), c.getDouble(5), c.getDouble(6),
                        c.getDouble(7), c.getLong(8), c.getLong(9));
            }
        }
        return null;
    }

    public double findVtbDepositBaseRate(long productId, double amount, int months) {
        String sql = "SELECT base_rate FROM vtb_deposit_rate_rules " +
                "WHERE product_id = ? AND term_months = ? AND min_amount <= ? AND max_amount >= ? " +
                "ORDER BY min_amount DESC LIMIT 1";
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, new String[]{
                String.valueOf(productId), String.valueOf(months),
                String.valueOf(amount), String.valueOf(amount)})) {
            if (c.moveToFirst()) return c.getDouble(0);
        }
        return -1.0;
    }

    public MortgageRateRule getMortgageProgramRule(long productId, String programCode, String regionGroup) {
        String sql = "SELECT base_rate, min_rate, max_loan, min_down_payment_pct, min_property_price, " +
                "min_term_months, max_term_months FROM mortgage_program_rules " +
                "WHERE product_id = ? AND program_code = ? AND region_group = ? LIMIT 1";
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, new String[]{
                String.valueOf(productId), programCode, regionGroup})) {
            if (c.moveToFirst()) {
                return new MortgageRateRule(c.getDouble(0), c.getDouble(1), c.getDouble(2),
                        c.getDouble(3), c.getDouble(4), c.getInt(5), c.getInt(6));
            }
        }
        return null;
    }

    public MortgageRateRule getFamilyMortgageRule(long productId, String regionGroup, int children) {
        String sql = "SELECT rate, rate, max_loan, min_down_payment_pct, min_property_price, " +
                "min_term_months, max_term_months FROM family_mortgage_rules " +
                "WHERE product_id = ? AND region_group = ? AND min_children <= ? AND max_children >= ? " +
                "LIMIT 1";
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, new String[]{
                String.valueOf(productId), regionGroup, String.valueOf(children), String.valueOf(children)})) {
            if (c.moveToFirst()) {
                return new MortgageRateRule(c.getDouble(0), c.getDouble(1), c.getDouble(2),
                        c.getDouble(3), c.getDouble(4), c.getInt(5), c.getInt(6));
            }
        }
        return null;
    }

    public double getMortgageAdjustment(long productId, String programCode, String adjustmentCode) {
        String sql = "SELECT rate_delta FROM mortgage_adjustments " +
                "WHERE product_id = ? AND program_code = ? AND adjustment_code = ? LIMIT 1";
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, new String[]{
                String.valueOf(productId), programCode, adjustmentCode})) {
            if (c.moveToFirst()) return c.getDouble(0);
        }
        return 0.0;
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