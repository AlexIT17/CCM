package com.example.ccm;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ccm.calc.CreditCalculator;
import com.example.ccm.calc.CreditResult;
import com.example.ccm.calc.DepositCalculator;
import com.example.ccm.calc.DepositResult;
import com.example.ccm.calc.MortgageCalculator;
import com.example.ccm.data.Product;
import com.example.ccm.data.RateRule;
import com.example.ccm.data.Repository;
import com.example.ccm.data.SourceInfo;

import java.util.List;
import java.util.Locale;

public class BankDetailActivity extends BaseActivity {

    private static final int COLOR_HINT    = 0xFF666666; // серый — справка
    private static final int COLOR_WARNING = 0xFFD32F2F; // красный — предупреждение

    private Repository repo;
    private Product product;
    private String type;
    private long bankId;
    private String bankName;

    private EditText etAmount, etTerm, etRate;
    private TextView tvResult, tvSourceInfo, tvWarning;

    private boolean userEditedRate = false;
    private boolean suppressRateWatcher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type     = getIntent().getStringExtra("type");
        bankId   = getIntent().getLongExtra("bankId", -1);
        bankName = getIntent().getStringExtra("bankName");
        String colorHex = getIntent().getStringExtra("color");
        if (colorHex == null) colorHex = "#D9D9D9";

        setFramedContent(R.layout.content_detail, Color.parseColor(colorHex));

        ((TextView) findViewById(R.id.tvBank)).setText(bankName);
        ((TextView) findViewById(R.id.tvType)).setText(type);

        repo    = new Repository(this);
        product = repo.getProduct(bankId, type);

        etAmount     = findViewById(R.id.etAmount);
        etTerm       = findViewById(R.id.etTerm);
        etRate       = findViewById(R.id.etRate);
        tvResult     = findViewById(R.id.tvResult);
        tvSourceInfo = findViewById(R.id.tvSourceInfo);
        tvWarning    = findViewById(R.id.tvWarning);

        etAmount.setText("100000");
        etTerm.setText("12");

        // Первичное заполнение ставки и предупреждения — до подписки на watcher'ы
        suppressRateWatcher = true;
        applyRateFromDb(true);
        suppressRateWatcher = false;

        TextWatcher amountTermWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                userEditedRate = false;              // ставку снова берём из базы
                applyRateFromDb(false);
                updateLimitsWarning();
                recalculate();
            }
        };
        etAmount.addTextChangedListener(amountTermWatcher);
        etTerm.addTextChangedListener(amountTermWatcher);

        etRate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                if (suppressRateWatcher) return;
                userEditedRate = true;
                recalculate();
            }
        });

        // Первичная отрисовка справки/предупреждения
        updateLimitsWarning();
        recalculate();
    }

    /** Подставляет ставку из базы по текущим сумме и сроку. */
    private void applyRateFromDb(boolean force) {
        if (product == null) return;
        if (userEditedRate && !force) return;

        double amount = parseDouble(etAmount.getText().toString());
        int    term   = parseInt(etTerm.getText().toString());
        RateRule rule = repo.findMatchingRule(product.id, amount, term);
        if (rule == null) return; // вне диапазона — ставку не трогаем, предупреждение покажет updateLimitsWarning

        String newRate = String.format(Locale.US, "%.2f", rule.ratePercent);
        if (!newRate.equals(etRate.getText().toString())) {
            suppressRateWatcher = true;
            etRate.setText(newRate);
            suppressRateWatcher = false;
        }

        SourceInfo src = repo.getSource(rule.sourceId);
        if (src != null) {
            tvSourceInfo.setText("Ставка из базы\nИсточник: " + src.url
                    + "\nДата проверки: " + src.checkedDate
                    + "\nДиапазон правила: " + fmt(rule.minAmount) + "–" + fmt(rule.maxAmount)
                    + " ₽, " + rule.minTermMonths + "–" + rule.maxTermMonths + " мес.");
        }
    }

    /**
     * Показывает справку о допустимых диапазонах или предупреждение,
     * если текущий ввод выходит за их пределы.
     */
    private void updateLimitsWarning() {
        if (tvWarning == null) return;

        if (product == null) {
            tvWarning.setVisibility(View.GONE);
            return;
        }

        List<RateRule> rules = repo.getRateRules(product.id);
        if (rules.isEmpty()) {
            tvWarning.setVisibility(View.GONE);
            return;
        }

        // Общий допустимый диапазон по всем правилам продукта
        double minAmount = Double.MAX_VALUE, maxAmount = -1;
        int    minTerm   = Integer.MAX_VALUE, maxTerm  = -1;
        for (RateRule r : rules) {
            if (r.minAmount < minAmount) minAmount = r.minAmount;
            if (r.maxAmount > maxAmount) maxAmount = r.maxAmount;
            if (r.minTermMonths < minTerm) minTerm = r.minTermMonths;
            if (r.maxTermMonths > maxTerm) maxTerm = r.maxTermMonths;
        }

        double amount = parseDouble(etAmount.getText().toString());
        int    term   = parseInt(etTerm.getText().toString());

        boolean amountFilled = amount > 0;
        boolean termFilled   = term > 0;
        boolean amountOk     = amountFilled && amount >= minAmount && amount <= maxAmount;
        boolean termOk       = termFilled   && term   >= minTerm   && term   <= maxTerm;

        if (amountOk && termOk) {
            tvWarning.setTextColor(COLOR_HINT);
            tvWarning.setText(String.format(Locale.US,
                    "Допустимо у этого банка: сумма %s–%s ₽, срок %d–%d мес.",
                    fmt(minAmount), fmt(maxAmount), minTerm, maxTerm));
        } else {
            tvWarning.setTextColor(COLOR_WARNING);
            StringBuilder sb = new StringBuilder("Вне допустимых значений:");
            if (!amountOk) {
                sb.append("\n• Сумма должна быть в диапазоне ")
                        .append(fmt(minAmount)).append("–").append(fmt(maxAmount)).append(" ₽");
            }
            if (!termOk) {
                sb.append("\n• Срок должен быть в диапазоне ")
                        .append(minTerm).append("–").append(maxTerm).append(" мес.");
            }
            if (!amountFilled && !termFilled) {
                sb = new StringBuilder("Введите сумму и срок, чтобы увидеть расчёт.");
                tvWarning.setTextColor(COLOR_HINT);
            }
            tvWarning.setText(sb.toString());
        }
        tvWarning.setVisibility(View.VISIBLE);
    }

    private void recalculate() {
        double amount = parseDouble(etAmount.getText().toString());
        int    term   = parseInt(etTerm.getText().toString());
        double rate   = parseDouble(etRate.getText().toString());

        if (amount <= 0 || term <= 0 || rate <= 0) {
            tvResult.setText("Введите сумму, срок и ставку");
            return;
        }

        if ("Вклад".equals(type)) {
            DepositResult r = DepositCalculator.calculate(amount, rate, term);
            tvResult.setText(String.format(Locale.US,
                    "Итоговая сумма: %.2f ₽\nНачисленные проценты: %.2f ₽",
                    r.finalAmount, r.interest));
        } else if ("Ипотека".equals(type)) {
            CreditResult r = MortgageCalculator.calculate(amount, rate, term);
            tvResult.setText(String.format(Locale.US,
                    "Ежемесячный платёж: %.2f ₽\nВсего выплат: %.2f ₽\nПереплата: %.2f ₽",
                    r.monthlyPayment, r.totalPayment, r.overpayment));
        } else {
            CreditResult r = CreditCalculator.calculate(amount, rate, term);
            tvResult.setText(String.format(Locale.US,
                    "Ежемесячный платёж: %.2f ₽\nВсего выплат: %.2f ₽\nПереплата: %.2f ₽",
                    r.monthlyPayment, r.totalPayment, r.overpayment));
        }
    }

    private static double parseDouble(String s) {
        try { return Double.parseDouble(s.trim().replace(',', '.')); }
        catch (Exception e) { return 0; }
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private static String fmt(double v) {
        return String.format(Locale.US, "%,.0f", v);
    }
}