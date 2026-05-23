package com.example.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private FirebaseManager firebaseManager;

    // Tài khoản mặc định để test (đã đăng ký trên Firebase Console)
    private static final String DEFAULT_EMAIL = "admin@smartlock.com";
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseManager = FirebaseManager.getInstance();

        initViews();
        setupListeners();

        // Kiểm tra đã đăng nhập chưa
        if (firebaseManager.getCurrentUser() != null) {
            // Đã đăng nhập, chuyển thẳng vào màn hình chính
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Xóa sạch kí tự cũ / rác trong các ô nhập liệu khi vào lại màn hình này
        if (edtEmail != null) edtEmail.setText("");
        if (edtPassword != null) edtPassword.setText("");
        
        // Trả nút đăng nhập về trạng thái bình thường
        if (btnLogin != null) {
            btnLogin.setText("ĐĂNG NHẬP");
            btnLogin.setEnabled(true);
        }
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void setDefaultValues() {
        // Đặt giá trị mặc định để test
        edtEmail.setText(DEFAULT_EMAIL);
        edtPassword.setText(DEFAULT_PASSWORD);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        tvForgotPassword.setOnClickListener(v -> showForgotPassword());
    }

    private void performLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra email không được để trống
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu không được để trống
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        // Hiệu ứng loading
        btnLogin.setText("ĐANG ĐĂNG NHẬP...");
        btnLogin.setEnabled(false);

        // Đăng nhập qua Firebase
        firebaseManager.login(email, password, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "✅ Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // Chuyển sang màn hình chonj thiết bị
                Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "❌ " + error, Toast.LENGTH_SHORT).show();
                btnLogin.setText("ĐĂNG NHẬP");
                btnLogin.setEnabled(true);

                Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                btnLogin.startAnimation(shake);
            }
        });
    }

    private void showRegister() {
        Toast.makeText(this, "🔧 Tính năng đang phát triển\nVui lòng liên hệ Admin để đăng ký", Toast.LENGTH_LONG).show();
    }

    private void showForgotPassword() {
        Toast.makeText(this, "🔧 Tính năng đang phát triển\nVui lòng liên hệ Admin để lấy lại mật khẩu", Toast.LENGTH_LONG).show();
    }
}