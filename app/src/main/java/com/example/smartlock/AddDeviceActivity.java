package com.example.smartlock;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartlock.models.Device;
import com.example.smartlock.utils.EncryptionHelper;

public class AddDeviceActivity extends AppCompatActivity {

    private EditText edtDeviceName, edtDeviceSerial, edtPin;
    private Button btnAdd;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        firebaseManager = FirebaseManager.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtDeviceName = findViewById(R.id.edt_device_name);
        edtDeviceSerial = findViewById(R.id.edt_device_serial);
        edtPin = findViewById(R.id.edt_pin);
        btnAdd = findViewById(R.id.btn_add);
    }

    private void setupListeners() {
        btnAdd.setOnClickListener(v -> addDevice());
    }

    private void addDevice() {
        String deviceName = edtDeviceName.getText().toString().trim();
        String deviceSerial = edtDeviceSerial.getText().toString().trim();
        String pin = edtPin.getText().toString().trim();

        if (TextUtils.isEmpty(deviceName)) {
            edtDeviceName.setError("Vui lòng nhập tên thiết bị");
            return;
        }
        if (TextUtils.isEmpty(deviceSerial)) {
            edtDeviceSerial.setError("Vui lòng nhập số serial");
            return;
        }
        if (TextUtils.isEmpty(pin) || pin.length() != 6) {
            edtPin.setError("Mã PIN phải có 6 số");
            return;
        }

        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceId = deviceSerial; // Dùng serial làm ID
        String pinHash = EncryptionHelper.hashPin(pin);

        Device device = new Device(deviceId, deviceName, deviceSerial, pinHash);

        btnAdd.setEnabled(false);
        btnAdd.setText("ĐANG THÊM...");

        firebaseManager.getDatabaseRef()
                .child("users")
                .child(userId)
                .child("devices")
                .child(deviceId)
                .setValue(device)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddDeviceActivity.this, "✅ Thêm thiết bị thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddDeviceActivity.this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnAdd.setEnabled(true);
                    btnAdd.setText("THÊM");
                });
    }
}