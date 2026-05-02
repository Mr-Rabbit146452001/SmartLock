package com.example.smartlock;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // Interface callback
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }

    // Đăng nhập
    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                        callback.onSuccess(currentUser);
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    // Đăng ký
    public void register(String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                        callback.onSuccess(currentUser);
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    // Đăng xuất
    public void logout() {
        mAuth.signOut();
        currentUser = null;
    }

    // Lấy user hiện tại
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Lấy userId hiện tại
    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getUid() : null;
    }

    // Lấy Database Reference
    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    // Cập nhật trạng thái khóa
    public void updateDoorStatus(boolean isLocked) {
        if (currentUser == null) return;
        String userId = currentUser.getUid();
        databaseRef.child("doors").child(userId).child("isLocked").setValue(isLocked);
    }
    // Đăng kí tài khoản
    public void saveUserToDatabase(String userId, String email, String fullName, String phone, String role) {
        DatabaseReference userRef = databaseRef.child("users").child(userId);
        userRef.child("email").setValue(email);
        userRef.child("fullName").setValue(fullName);
        userRef.child("phone").setValue(phone);
        userRef.child("role").setValue(role);
        userRef.child("createdAt").setValue(System.currentTimeMillis());
    }

    // Thêm log
    public void addLog(String action, String method, boolean success) {
        if (currentUser == null) return;
        String logId = databaseRef.child("logs").push().getKey();
        if (logId != null) {
            databaseRef.child("logs").child(logId).setValue(new LogEntry(
                    currentUser.getUid(),
                    currentUser.getEmail(),
                    action,
                    method,
                    success,
                    System.currentTimeMillis()
            ));
        }
    }

    // Class LogEntry
    public static class LogEntry {
        public String userId;
        public String userEmail;
        public String action;
        public String method;
        public boolean success;
        public long timestamp;

        public LogEntry(String userId, String userEmail, String action,
                        String method, boolean success, long timestamp) {
            this.userId = userId;
            this.userEmail = userEmail;
            this.action = action;
            this.method = method;
            this.success = success;
            this.timestamp = timestamp;
        }
    }
}
