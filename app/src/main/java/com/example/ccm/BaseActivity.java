package com.example.ccm;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public abstract class BaseActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnForward;

    // Главный экран переопределяет: с него назад идти некуда
    protected boolean isRoot() {
        return false;
    }

    protected void setFramedContent(@LayoutRes int layout, @ColorRes int bandColor) {
        setContentView(R.layout.activity_frame);

        // Чёрный статус-бар со светлыми значками
        getWindow().setStatusBarColor(Color.BLACK);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        // Отступы под статус-бар и навигацию (на новых Android контент идёт под ними)
        View root = findViewById(R.id.root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, bars.bottom);
            return insets;
        });

        int color = ContextCompat.getColor(this, bandColor);
        findViewById(R.id.bandLeft).setBackgroundColor(color);
        findViewById(R.id.bandRight).setBackgroundColor(color);
        getLayoutInflater().inflate(layout, (FrameLayout) findViewById(R.id.content), true);

        btnBack = findViewById(R.id.btnBack);
        btnForward = findViewById(R.id.btnForward);
        btnBack.setOnClickListener(v -> goBack());
        btnForward.setOnClickListener(v -> goForward());

        // Системная кнопка «назад» ведёт себя так же, как стрелка
        if (!isRoot()) {
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    goBack();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (btnBack != null) {
            setActive(btnBack, !isRoot());
            setActive(btnForward, NavHistory.hasForward());
        }
    }

    private void setActive(ImageButton button, boolean active) {
        button.setEnabled(active);
        button.setAlpha(active ? 1f : 0.3f);
    }

    private void goBack() {
        if (isRoot()) return;
        NavHistory.push(new Intent(getIntent()));
        finish();
    }

    private void goForward() {
        Intent next = NavHistory.pop();
        if (next != null) {
            super.startActivity(next);
        }
    }

    // Обычный переход вперёд по приложению сбрасывает «вперёд», как в браузере
    @Override
    public void startActivity(Intent intent) {
        NavHistory.clear();
        super.startActivity(intent);
    }
}