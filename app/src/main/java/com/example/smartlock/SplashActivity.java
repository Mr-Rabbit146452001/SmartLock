package com.example.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvTitle = findViewById(R.id.tv_splash_title);
        View layoutFooter = findViewById(R.id.layout_splash_footer);

        // Lấy mật độ màn hình để tính toán khoảng cách trượt (translation) chuẩn xác theo dp
        float density = getResources().getDisplayMetrics().density;
        float titleStartTranslation = 30 * density; // trượt lên 30dp
        float footerStartTranslation = 20 * density; // trượt lên 20dp

        // Cấu hình trạng thái ban đầu (invisible và lệch vị trí)
        if (tvTitle != null) {
            tvTitle.setAlpha(0.0f);
            tvTitle.setTranslationY(titleStartTranslation);
            tvTitle.setScaleX(0.92f);
            tvTitle.setScaleY(0.92f);
        }

        if (layoutFooter != null) {
            layoutFooter.setAlpha(0.0f);
            layoutFooter.setTranslationY(footerStartTranslation);
        }

        // Bắt đầu hoạt ảnh động tinh tế cho tiêu đề (Chữ SMART LOCK)
        if (tvTitle != null) {
            tvTitle.animate()
                    .alpha(1.0f)
                    .translationY(0.0f)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(1200)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        // Bắt đầu hoạt ảnh động cho phần Footer (Version và HKC Group) trễ hơn 500ms (Staggered effect)
        if (layoutFooter != null) {
            layoutFooter.animate()
                    .alpha(1.0f)
                    .translationY(0.0f)
                    .setDuration(1000)
                    .setStartDelay(500)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        // Chuyển màn hình sau khi hoàn tất toàn bộ hoạt ảnh (2.5 giây)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (FirebaseManager.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
            // Tắt hiệu ứng chuyển màn hình mặc định để tạo sự mượt mà
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 2500);
    }
}