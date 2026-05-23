package com.example.smartlock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private Button btnFingerprint, btnFace, btnPin;
    private String deviceId, deviceName, deviceSerial;

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

        // Đọc thông tin thiết bị từ Intent
        deviceId = getIntent().getStringExtra("device_id");
        deviceName = getIntent().getStringExtra("device_name");
        deviceSerial = getIntent().getStringExtra("device_serial");

        SharedPreferences sharedPref = getSharedPreferences("SmartLockPrefs", Context.MODE_PRIVATE);

        if (deviceId != null) {
            // Lưu vào SharedPreferences để dùng cho các lần mở app sau
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("last_device_id", deviceId);
            editor.putString("last_device_name", deviceName);
            editor.putString("last_device_serial", deviceSerial);
            editor.apply();
        } else {
            // Tải lại thiết bị cuối cùng đã chọn
            deviceId = sharedPref.getString("last_device_id", null);
            deviceName = sharedPref.getString("last_device_name", null);
            deviceSerial = sharedPref.getString("last_device_serial", null);
        }

        // Nếu chưa từng chọn thiết bị nào (ví dụ tài khoản mới tinh), đưa họ qua màn hình chọn thiết bị
        if (deviceId == null) {
            startActivity(new Intent(MainActivity.this, DeviceListActivity.class));
            finish();
            return;
        }

        setupFingerprintUnlock();

        btnFace.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FaceUnlockActivity.class);
            intent.putExtra("device_id", deviceId);
            intent.putExtra("device_name", deviceName);
            startActivity(intent);
        });

        btnPin.setOnClickListener(v -> {
            // Nhấn MÃ PIN sẽ đi thẳng tới màn hình nhập PIN, không đi qua danh sách thiết bị
            Intent intent = new Intent(MainActivity.this, DevicePinActivity.class);
            intent.putExtra("device_id", deviceId);
            intent.putExtra("device_name", deviceName);
            intent.putExtra("device_serial", deviceSerial);
            startActivity(intent);
        });
    }

    private void setupFingerprintUnlock() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            btnFingerprint.setEnabled(false);
            btnFingerprint.setAlpha(0.5f);
            Log.e("Biometrics", "Biometrics not supported or not set up. Code: " + canAuthenticate);
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    Toast.makeText(MainActivity.this, "Lỗi xác thực: " + errString, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Xác thực thành công -> Gửi lệnh mở khóa qua Firebase
                FirebaseManager.getInstance().getDatabaseRef()
                        .child("command")
                        .child("unlock")
                        .setValue(true)
                        .addOnSuccessListener(aVoid -> {
                            FirebaseManager.getInstance().addLog("Mở khóa", "Vân tay (App)", true);
                            Toast.makeText(MainActivity.this, "🔓 Đã gửi lệnh mở khóa thành công!", Toast.LENGTH_SHORT).show();
                            
                            // Chuyển trực tiếp sang HomeActivity
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("device_id", deviceId);
                            intent.putExtra("device_name", deviceName);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Lỗi kết nối Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Xác thực thất bại, thử lại!", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Xác thực Vân Tay")
                .setSubtitle("Vui lòng đặt ngón tay vào cảm biến vân tay của điện thoại")
                .setNegativeButtonText("Hủy")
                .build();

        btnFingerprint.setOnClickListener(v -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }
}