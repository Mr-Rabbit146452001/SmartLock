package com.example.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtFullname, edtPhone;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseManager = FirebaseManager.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtFullname = findViewById(R.id.edt_fullname);
        edtPhone = findViewById(R.id.edt_phone);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void performRegister() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String fullname = edtFullname.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        if (TextUtils.isEmpty(fullname)) {
            edtFullname.setError("Vui lòng nhập họ tên");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("ĐANG ĐĂNG KÝ...");

        // Đăng ký qua Firebase Authentication
        firebaseManager.register(email, password, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                String userId = user.getUid();

                // Lưu thông tin user vào Realtime Database
                firebaseManager.saveUserToDatabase(userId, email, fullname, phone, "member");

                Toast.makeText(RegisterActivity.this, "✅ Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, "❌ Đăng ký thất bại: " + error, Toast.LENGTH_LONG).show();
                btnRegister.setEnabled(true);
                btnRegister.setText("ĐĂNG KÝ");
            }
        });
    }
}