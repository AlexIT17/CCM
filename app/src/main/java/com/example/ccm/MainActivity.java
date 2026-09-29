package com.example.ccm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFramedContent(R.layout.content_main, R.color.gray_light);

        Button btnCredit = findViewById(R.id.btnCredit);
        Button btnDeposit = findViewById(R.id.btnDeposit);
        Button btnMortgage = findViewById(R.id.btnMortgage);

        btnCredit.setOnClickListener(v -> open("Кредит"));
        btnDeposit.setOnClickListener(v -> open("Вклад"));
        btnMortgage.setOnClickListener(v -> open("Ипотека"));
    }

    @Override
    protected boolean isRoot() {
        return true;
    }

    private void open(String type) {
        Intent intent = new Intent(this, BankListActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}