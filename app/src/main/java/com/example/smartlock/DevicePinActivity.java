package com.example.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartlock.utils.EncryptionHelper;

public class DevicePinActivity extends AppCompatActivity {

    private TextView tvDeviceName, tvDeviceSerial, tvPinDisplay, tvError, tvBack;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnClear, btnConfirm;

    private StringBuilder pinInput = new StringBuilder();
    private String deviceId, deviceName, deviceSerial;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pin);

        firebaseManager = FirebaseManager.getInstance();

        deviceId = getIntent().getStringExtra("device_id");
        deviceName = getIntent().getStringExtra("device_name");
        deviceSerial = getIntent().getStringExtra("device_serial");

        initViews();
        setupListeners();

        tvDeviceName.setText(deviceName);
        tvDeviceSerial.setText(deviceSerial);
    }

    private void initViews() {
        tvDeviceName = findViewById(R.id.tv_device_name);
        tvDeviceSerial = findViewById(R.id.tv_device_serial);
        tvPinDisplay = findViewById(R.id.tv_pin_display);
        tvError = findViewById(R.id.tv_error);

        btn0 = findViewById(R.id.btn_0);
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        btn8 = findViewById(R.id.btn_8);
        btn9 = findViewById(R.id.btn_9);
        btnClear = findViewById(R.id.btn_clear);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvBack = findViewById(R.id.tv_back);
    }

    private void setupListeners() {
        btn0.setOnClickListener(v -> addDigit('0'));
        btn1.setOnClickListener(v -> addDigit('1'));
        btn2.setOnClickListener(v -> addDigit('2'));
        btn3.setOnClickListener(v -> addDigit('3'));
        btn4.setOnClickListener(v -> addDigit('4'));
        btn5.setOnClickListener(v -> addDigit('5'));
        btn6.setOnClickListener(v -> addDigit('6'));
        btn7.setOnClickListener(v -> addDigit('7'));
        btn8.setOnClickListener(v -> addDigit('8'));
        btn9.setOnClickListener(v -> addDigit('9'));

        btnClear.setOnClickListener(v -> {
            if (pinInput.length() > 0) {
                pinInput.deleteCharAt(pinInput.length() - 1);
                updatePinDisplay();
                tvError.setVisibility(android.view.View.GONE);
            }
        });

        btnConfirm.setOnClickListener(v -> verifyPin());
        tvBack.setOnClickListener(v -> finish());
    }

    private void addDigit(char digit) {
        if (pinInput.length() < 6) {
            pinInput.append(digit);
            updatePinDisplay();
            tvError.setVisibility(android.view.View.GONE);
        }
    }

    private void updatePinDisplay() {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < pinInput.length(); i++) {
            display.append("● ");
        }
        tvPinDisplay.setText(display.toString());
    }

    private void verifyPin() {
        if (pinInput.length() < 6) {
            showError("Vui lòng nhập đủ 6 số");
            return;
        }

        String enteredPin = pinInput.toString();
        String enteredPinHash = EncryptionHelper.hashPin(enteredPin);

        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            showError("Chưa đăng nhập");
            return;
        }

        btnConfirm.setEnabled(false);
        btnConfirm.setText("ĐANG XÁC THỰC...");

        firebaseManager.getDatabaseRef()
                .child("users")
                .child(userId)
                .child("devices")
                .child(deviceId)
                .child("pinHash")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String savedPinHash = snapshot.getValue(String.class);
                    if (savedPinHash != null && savedPinHash.equals(enteredPinHash)) {
                        // Xác thực thành công
                        Toast.makeText(this, "✅ Xác thực thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DevicePinActivity.this, HomeActivity.class);
                        intent.putExtra("device_id", deviceId);
                        intent.putExtra("device_name", deviceName);
                        startActivity(intent);
                        finish();
                    } else {
                        showError("❌ Sai mã PIN");
                        pinInput = new StringBuilder();
                        updatePinDisplay();
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("XÁC NHẬN");
                    }
                })
                .addOnFailureListener(e -> {
                    showError("Lỗi: " + e.getMessage());
                    btnConfirm.setEnabled(true);
                    btnConfirm.setText("XÁC NHẬN");
                });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(android.view.View.VISIBLE);
        new Handler().postDelayed(() -> tvError.setVisibility(android.view.View.GONE), 3000);
    }
}