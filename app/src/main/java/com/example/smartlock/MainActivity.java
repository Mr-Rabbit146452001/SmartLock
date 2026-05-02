package com.example.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnFingerprint, btnFace, btnPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFingerprint = findViewById(R.id.btn_fingerprint);
        btnFace = findViewById(R.id.btn_face);
        btnPin = findViewById(R.id.btn_pin);

        // Kiểm tra đã đăng nhập chưa? Chưa thì chuyển sang Login
        if (FirebaseManager.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        btnFingerprint.setOnClickListener(v -> {
            // TODO: Xác thực vân tay
        });

        btnFace.setOnClickListener(v -> {
            // TODO: Xác thực khuôn mặt
        });

        btnPin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DeviceListActivity.class));
        });
    }
}