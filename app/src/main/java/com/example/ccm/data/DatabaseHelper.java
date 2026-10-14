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

    /**
     * Версия базы.
     * Поднимайте на 1 каждый раз, когда правите seed.sql
     * или схему — тогда при запуске база пересоздастся.
     */
    private static final int VERSION = 3;

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
    public void onCreate(SQLiteDatabase db) {
        createSchema(db);
        loadSeedFromAssets(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS rate_rules");
        db.execSQL("DROP TABLE IF EXISTS source_info");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS banks");
        onCreate(db);
    }

    // ---------- схема ----------

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
                "min_amount REAL NOT NULL," +
                "max_amount REAL NOT NULL," +
                "min_term_months INTEGER NOT NULL," +
                "max_term_months INTEGER NOT NULL," +
                "source_id INTEGER NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id)," +
                "FOREIGN KEY (source_id)  REFERENCES source_info(id))");
    }

    // ---------- данные из assets/seed.sql ----------

    private void loadSeedFromAssets(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        try (InputStream is = context.getAssets().open("seed.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // Пустые строки и комментарии пропускаем
                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;

                sql.append(line).append('\n');

                // Конец инструкции — точка с запятой в конце строки.
                // Считаем так: последний непробельный символ == ';'
                if (trimmed.endsWith(";")) {
                    String statement = sql.toString().trim();
                    if (!statement.isEmpty()) {
                        db.execSQL(statement);
                    }
                    sql.setLength(0);
                }
            }

            // Защита от забытой точки с запятой в конце файла
            if (sql.toString().trim().length() > 0) {
                db.execSQL(sql.toString().trim());
            }

        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать assets/seed.sql", e);
        }
    }
}