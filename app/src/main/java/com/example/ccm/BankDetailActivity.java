package com.example.ccm;

import android.os.Bundle;
import android.widget.TextView;

public class BankDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int color = getIntent().getIntExtra("color", R.color.gray_light);
        setFramedContent(R.layout.content_detail, color);

        TextView tvBank = findViewById(R.id.tvBank);
        TextView tvType = findViewById(R.id.tvType);

        tvBank.setText(getIntent().getStringExtra("bank"));
        tvType.setText(getIntent().getStringExtra("type"));
    }
}