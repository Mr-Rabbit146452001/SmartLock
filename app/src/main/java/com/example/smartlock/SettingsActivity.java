package com.example.smartlock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.smartlock.utils.PinManager;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private PinManager pinManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pinManager = PinManager.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 1. Đổi mã PIN
        Button btnChangePin = findViewById(R.id.btn_change_pin);
        btnChangePin.setOnClickListener(v -> showChangePinDialog());

        // 1b. Chọn thiết bị (Nằm trong phần bảo mật)
        androidx.cardview.widget.CardView cardSelectDevice = findViewById(R.id.card_select_device);
        cardSelectDevice.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DeviceListActivity.class);
            startActivity(intent);
        });

        // 2. Ngưỡng cảnh báo
        Spinner spinnerThreshold = findViewById(R.id.spinner_threshold);
        String[] thresholds = {"30 giây", "60 giây", "90 giây", "120 giây"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, thresholds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThreshold.setAdapter(adapter);

        // 3. Thông báo đẩy
        SwitchCompat switchNotification = findViewById(R.id.switch_notification);

        // 4. Số lần nhập sai tối đa
        Spinner spinnerMaxAttempts = findViewById(R.id.spinner_max_attempts);
        String[] attempts = {"3 lần", "5 lần", "7 lần", "10 lần"};
        ArrayAdapter<String> attemptsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, attempts);
        attemptsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaxAttempts.setAdapter(attemptsAdapter);

        // 6. Chế độ hoạt động
        RadioGroup rgMode = findViewById(R.id.rg_mode);
        RadioButton rbOnline = findViewById(R.id.rb_online);
        RadioButton rbOffline = findViewById(R.id.rb_offline);
        rbOnline.setChecked(true);

        // 7. Dữ liệu
        Button btnExport = findViewById(R.id.btn_export_history);
        Button btnBackup = findViewById(R.id.btn_backup);
        Button btnRestore = findViewById(R.id.btn_restore);

        btnExport.setOnClickListener(v -> Toast.makeText(this, "Đã xuất lịch sử", Toast.LENGTH_SHORT).show());
        btnBackup.setOnClickListener(v -> Toast.makeText(this, "Đã sao lưu", Toast.LENGTH_SHORT).show());
        btnRestore.setOnClickListener(v -> Toast.makeText(this, "Đã khôi phục", Toast.LENGTH_SHORT).show());

        // 8. Giao diện

        RadioGroup rgTheme = findViewById(R.id.rg_theme);
        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int newMode;
            if (checkedId == R.id.rb_light) {
                newMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.rb_dark) {
                newMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            // Chỉ recreate nếu chế độ thay đổi
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                AppCompatDelegate.setDefaultNightMode(newMode);
                recreate();
            }
        });
        Spinner spinnerLanguage = findViewById(R.id.spinner_language);
        String[] languages = {"Tiếng Việt", "English"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(langAdapter);

        // 9. Hỗ trợ
        Button btnFaq = findViewById(R.id.btn_faq);
        Button btnGuide = findViewById(R.id.btn_guide);
        Button btnContact = findViewById(R.id.btn_contact);

        btnFaq.setOnClickListener(v -> Toast.makeText(this, "FAQ", Toast.LENGTH_SHORT).show());
        btnGuide.setOnClickListener(v -> Toast.makeText(this, "Hướng dẫn", Toast.LENGTH_SHORT).show());
        btnContact.setOnClickListener(v -> Toast.makeText(this, "Liên hệ: support@smartlock.com", Toast.LENGTH_SHORT).show());

        // 10. Thông tin
        TextView tvVersion = findViewById(R.id.tv_version);
        TextView btnPrivacy = findViewById(R.id.btn_privacy);
        btnPrivacy.setOnClickListener(v -> Toast.makeText(this, "Chính sách bảo mật", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void applyTheme(boolean isDark) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate();
    }

    private void showChangePinDialog() {
        SharedPreferences sharedPref = getSharedPreferences("SmartLockPrefs", Context.MODE_PRIVATE);
        String deviceId = sharedPref.getString("last_device_id", null);
        String userId = FirebaseManager.getInstance().getCurrentUserId();

        if (deviceId == null || userId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin thiết bị hoặc tài khoản!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo EditText nhập PIN mới dạng bàn phím số
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance()); // Ẩn kí tự bằng dấu chấm bảo mật
        input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(6)});
        input.setHint("Nhập 6 chữ số mới");
        input.setGravity(android.view.Gravity.CENTER);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);
        layout.addView(input);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thay đổi mã PIN")
                .setMessage("Nhập mã PIN 6 chữ số mới cho thiết bị hiện tại:")
                .setView(layout)
                .setPositiveButton("XÁC NHẬN", null) // Bấm xác nhận để kiểm tra độ dài trước khi dismiss
                .setNegativeButton("HỦY", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        dialog.show();

        // Xử lý kiểm tra dữ liệu trước khi lưu
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String newPin = input.getText().toString().trim();
            if (newPin.length() != 6) {
                input.setError("Mã PIN phải đủ 6 chữ số!");
                return;
            }

            // Hashing mã PIN mới
            String pinHash = com.example.smartlock.utils.EncryptionHelper.hashPin(newPin);

            // Cập nhật mã PIN lên Firebase Database
            FirebaseManager.getInstance().getDatabaseRef()
                    .child("users")
                    .child(userId)
                    .child("devices")
                    .child(deviceId)
                    .child("pinHash")
                    .setValue(pinHash)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SettingsActivity.this, "✅ Thay đổi mã PIN thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SettingsActivity.this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}