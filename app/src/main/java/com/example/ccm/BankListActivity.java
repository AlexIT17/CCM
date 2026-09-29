package com.example.ccm;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class BankListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFramedContent(R.layout.content_banks, R.color.gray_light);

        String type = getIntent().getStringExtra("type");
        LinearLayout container = findViewById(R.id.banksContainer);

        float density = getResources().getDisplayMetrics().density;
        int height = (int) (76 * density);
        int gap = (int) (20 * density);

        for (Bank bank : Bank.BANKS) {
            AppCompatButton btn = new AppCompatButton(this);
            btn.setText(bank.name);
            btn.setAllCaps(false);
            btn.setTextSize(24);
            btn.setTypeface(btn.getTypeface(), android.graphics.Typeface.BOLD);
            btn.setTextColor(ContextCompat.getColor(this, bank.colorRes));
            btn.setBackgroundResource(R.drawable.btn_selector);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            lp.topMargin = gap;
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> {
                Intent intent = new Intent(BankListActivity.this, BankDetailActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("bank", bank.name);
                intent.putExtra("color", bank.colorRes);
                startActivity(intent);
            });

            container.addView(btn);
        }
    }
}