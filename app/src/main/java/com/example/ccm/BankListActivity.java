package com.example.ccm;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.ccm.data.Bank;
import com.example.ccm.data.Repository;

import java.util.List;

public class BankListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFramedContent(R.layout.content_banks,
                ContextCompat.getColor(this, R.color.gray_light));

        String type = getIntent().getStringExtra("type");
        LinearLayout container = findViewById(R.id.banksContainer);

        Repository repo = new Repository(this);
        List<Bank> banks = repo.getBanksForType(type);

        float density = getResources().getDisplayMetrics().density;
        int height = (int) (76 * density);
        int gap = (int) (20 * density);

        for (Bank bank : banks) {
            AppCompatButton btn = new AppCompatButton(this);
            btn.setText(bank.name);
            btn.setAllCaps(false);
            btn.setTextSize(24);
            btn.setTypeface(btn.getTypeface(), android.graphics.Typeface.BOLD);
            btn.setTextColor(bank.color());          // цвет из БД
            btn.setBackgroundResource(R.drawable.btn_selector);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            lp.topMargin = gap;
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> {
                Intent intent = new Intent(BankListActivity.this, BankDetailActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("bankId", bank.id);
                intent.putExtra("bankName", bank.name);
                intent.putExtra("color", bank.colorHex);
                startActivity(intent);
            });

            container.addView(btn);
        }
    }
}