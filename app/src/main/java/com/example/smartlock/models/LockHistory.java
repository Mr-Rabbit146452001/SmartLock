package com.example.smartlock.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lock_history")
public class LockHistory {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;           // Người thực hiện (userId)
    private String userName;         // Tên người thực hiện
    private String action;           // "open", "close", "pin_changed"
    private String method;           // "fingerprint", "face", "pin", "system"
    private boolean success;         // Thành công hay thất bại
    private long timestamp;          // Thời gian

    // Constructor mặc định
    public LockHistory() {}

    // Constructor
    public LockHistory(String userId, String userName, String action, String method, boolean success) {
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.method = method;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor đơn giản cho mở/khóa
    public LockHistory(String userId, String userName, String action, boolean success) {
        this(userId, userName, action, "system", success);
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}