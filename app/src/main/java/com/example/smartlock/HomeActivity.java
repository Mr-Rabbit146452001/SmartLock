package com.example.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private TextView tvLockStatus, tvLastOpen;
    private Button btnControl, btnLogout, btnManageUsers, btnSettings;
    private FirebaseManager firebaseManager;

    private boolean isLocked = true;
    private String lastOpenTime = "Chưa có dữ liệu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseManager = FirebaseManager.getInstance();

        initViews();
        setupListeners();
        listenDoorStatus();  // Lắng nghe trạng thái từ Firebase
        updateUI();
    }

    private void initViews() {
        tvLockStatus = findViewById(R.id.tv_lock_status);
        tvLastOpen = findViewById(R.id.tv_last_open);
        btnControl = findViewById(R.id.btn_control);
        btnLogout = findViewById(R.id.btn_logout);
        btnManageUsers = findViewById(R.id.btn_manage_users);
        btnSettings = findViewById(R.id.btn_settings);

        if (btnManageUsers != null) {
            btnManageUsers.setVisibility(Button.GONE);
        }
    }

    private void setupListeners() {
        btnControl.setOnClickListener(v -> {
            if (isLocked) {
                sendUnlockCommand();  // Gửi lệnh MỞ KHÓA qua Firebase
            } else {
                sendLockCommand();    // Gửi lệnh KHÓA qua Firebase
            }
        });

        btnLogout.setOnClickListener(v -> {
            firebaseManager.logout();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserListActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    // Lắng nghe trạng thái khóa từ Firebase (ESP32 gửi lên)
    private void listenDoorStatus() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        firebaseManager.getDatabaseRef()
                .child("door")
                .child("isLocked")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean locked = snapshot.getValue(Boolean.class);
                        if (locked != null) {
                            isLocked = locked;
                            updateUI();

                            // Hiển thị thông báo trạng thái
                            String status = isLocked ? "🔒 CỬA ĐÃ KHÓA" : "🔓 CỬA ĐANG MỞ";
                            Toast.makeText(HomeActivity.this, status, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Lỗi: " + error.getMessage());
                    }
                });
    }

    // Gửi lệnh MỞ KHÓA lên Firebase
    private void sendUnlockCommand() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        btnControl.setText("⏳");
        btnControl.setEnabled(false);

        firebaseManager.getDatabaseRef()
                .child("command")
                .child("unlock")
                .setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "🔓 Đã gửi lệnh mở khóa!", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> {
                        btnControl.setText(isLocked ? "🔒" : "🔓");
                        btnControl.setEnabled(true);
                    }, 2000);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnControl.setText(isLocked ? "🔒" : "🔓");
                    btnControl.setEnabled(true);
                });
    }

    // Gửi lệnh KHÓA lên Firebase
    private void sendLockCommand() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        btnControl.setText("⏳");
        btnControl.setEnabled(false);

        firebaseManager.getDatabaseRef()
                .child("command")
                .child("lock")
                .setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "🔒 Đã gửi lệnh khóa!", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> {
                        btnControl.setText(isLocked ? "🔒" : "🔓");
                        btnControl.setEnabled(true);
                    }, 2000);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnControl.setText(isLocked ? "🔒" : "🔓");
                    btnControl.setEnabled(true);
                });
    }

    private void updateUI() {
        if (isLocked) {
            tvLockStatus.setText("ĐÃ KHÓA");
            tvLockStatus.setTextColor(getColor(R.color.error));
            btnControl.setText("🔒");
        } else {
            tvLockStatus.setText("ĐANG MỞ");
            tvLockStatus.setTextColor(getColor(R.color.success));
            btnControl.setText("🔓");
        }
        tvLastOpen.setText(lastOpenTime);
    }

    private String getCurrentTime() {
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }
}