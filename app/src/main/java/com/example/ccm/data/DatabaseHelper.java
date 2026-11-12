package com.example.ccm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "kvi.db";

    private static final int VERSION = 21;

    private static DatabaseHelper instance;
    private final Context context;

    public static synchronized DatabaseHelper get(Context c) {
        if (instance == null) instance = new DatabaseHelper(c.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSchema(db);
        loadSeedFromAssets(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mortgage_adjustments");
        db.execSQL("DROP TABLE IF EXISTS family_mortgage_rules");
        db.execSQL("DROP TABLE IF EXISTS mortgage_program_rules");
        db.execSQL("DROP TABLE IF EXISTS vtb_deposit_rate_rules");
        db.execSQL("DROP TABLE IF EXISTS vtb_deposit_settings");
        db.execSQL("DROP TABLE IF EXISTS deposit_rate_rules");
        db.execSQL("DROP TABLE IF EXISTS rate_rules");
        db.execSQL("DROP TABLE IF EXISTS source_info");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS banks");
        onCreate(db);
    }


    private void createSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE banks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "color TEXT NOT NULL," +
                "website TEXT)");

        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bank_id INTEGER NOT NULL," +
                "type TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "FOREIGN KEY (bank_id) REFERENCES banks(id))");

        db.execSQL("CREATE TABLE source_info (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "url TEXT NOT NULL," +
                "checked_date TEXT NOT NULL)");

        db.execSQL("CREATE TABLE rate_rules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER NOT NULL," +
                "rate REAL NOT NULL," +
                "client_segment TEXT NOT NULL DEFAULT 'ALL'," +
                "min_amount REAL NOT NULL," +
                "max_amount REAL NOT NULL," +
                "min_term_months INTEGER NOT NULL," +
                "max_term_months INTEGER NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id)  REFERENCES source_info(id))");

        db.execSQL("CREATE TABLE deposit_rate_rules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER NOT NULL," +
                "min_amount REAL NOT NULL," +
                "max_amount REAL NOT NULL," +
                "min_term_months INTEGER NOT NULL," +
                "max_term_months INTEGER NOT NULL," +
                "top_up INTEGER NOT NULL DEFAULT 0," +
                "rate REAL NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id) REFERENCES source_info(id))");

        db.execSQL("CREATE TABLE vtb_deposit_settings (" +
                "product_id INTEGER PRIMARY KEY," +
                "min_online_amount REAL NOT NULL," +
                "min_office_amount REAL NOT NULL," +
                "min_interest_balance REAL NOT NULL," +
                "min_term_months INTEGER NOT NULL," +
                "max_term_months INTEGER NOT NULL," +
                "max_ui_amount REAL NOT NULL," +
                "max_vklad_rate REAL NOT NULL," +
                "max_account_rate REAL NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "account_source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id) REFERENCES source_info(id)," +
                "FOREIGN KEY (account_source_id) REFERENCES source_info(id))");

        db.execSQL("CREATE TABLE vtb_deposit_rate_rules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER NOT NULL," +
                "term_months INTEGER NOT NULL," +
                "min_amount REAL NOT NULL," +
                "max_amount REAL NOT NULL," +
                "base_rate REAL NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id) REFERENCES source_info(id))");

        db.execSQL("CREATE TABLE mortgage_program_rules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER NOT NULL," +
                "program_code TEXT NOT NULL," +
                "region_group TEXT NOT NULL," +
                "base_rate REAL NOT NULL," +
                "min_rate REAL NOT NULL," +
                "max_loan REAL NOT NULL," +
                "min_down_payment_pct REAL NOT NULL," +
                "min_property_price REAL NOT NULL," +
                "min_term_months INTEGER NOT NULL," +
                "max_term_months INTEGER NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id) REFERENCES source_info(id))");

        db.execSQL("CREATE TABLE family_mortgage_rules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER NOT NULL," +
                "region_group TEXT NOT NULL," +
                "min_children INTEGER NOT NULL," +
                "max_children INTEGER NOT NULL," +
                "rate REAL NOT NULL," +
                "max_loan REAL NOT NULL," +
                "min_down_payment_pct REAL NOT NULL," +
                "min_property_price REAL NOT NULL," +
                "min_term_months INTEGER NOT NULL," +
                "max_term_months INTEGER NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id) REFERENCES source_info(id))");

        db.execSQL("CREATE TABLE mortgage_adjustments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER NOT NULL," +
                "program_code TEXT NOT NULL," +
                "adjustment_code TEXT NOT NULL," +
                "rate_delta REAL NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id) REFERENCES source_info(id))");

        db.execSQL("CREATE INDEX idx_products_bank_type ON products(bank_id, type)");
        db.execSQL("CREATE INDEX idx_rate_rules_product_segment ON rate_rules(product_id, client_segment)");
        db.execSQL("CREATE INDEX idx_deposit_rules_lookup ON deposit_rate_rules(product_id, top_up, min_term_months, max_term_months)");
        db.execSQL("CREATE INDEX idx_vtb_deposit_rules_lookup ON vtb_deposit_rate_rules(product_id, term_months, min_amount, max_amount)");
        db.execSQL("CREATE INDEX idx_mortgage_rules_lookup ON mortgage_program_rules(product_id, program_code, region_group)");
        db.execSQL("CREATE INDEX idx_family_mortgage_lookup ON family_mortgage_rules(product_id, region_group, min_children, max_children)");
        db.execSQL("CREATE INDEX idx_mortgage_adjustments_lookup ON mortgage_adjustments(product_id, program_code, adjustment_code)");
    }


    private void loadSeedFromAssets(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        try (InputStream is = context.getAssets().open("seed.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;

                sql.append(line).append('\n');

                if (trimmed.endsWith(";")) {
                    String statement = sql.toString().trim();
                    if (!statement.isEmpty()) {
                        db.execSQL(statement);
                    }
                    sql.setLength(0);
                }
            }

            if (sql.toString().trim().length() > 0) {
                db.execSQL(sql.toString().trim());
            }

        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать assets/seed.sql", e);
        }
    }
}