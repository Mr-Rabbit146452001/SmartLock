package com.example.smartlock.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "master_pin")
public class MasterPin {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String encryptedPin;      // Mã PIN đã mã hóa
    private String pinHash;           // Hash của PIN để xác thực
    private long lastChanged;         // Thời gian thay đổi gần nhất
    private String changedBy;         // Ai đã thay đổi (userId)
    private int failedAttempts;       // Số lần nhập sai
    private long lockedUntil;         // Thời gian khóa đến khi

    // Constructor mặc định
    public MasterPin() {}

    // Constructor
    public MasterPin(String encryptedPin, String pinHash) {
        this.encryptedPin = encryptedPin;
        this.pinHash = pinHash;
        this.lastChanged = System.currentTimeMillis();
        this.failedAttempts = 0;
        this.lockedUntil = 0;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEncryptedPin() { return encryptedPin; }
    public void setEncryptedPin(String encryptedPin) { this.encryptedPin = encryptedPin; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public long getLastChanged() { return lastChanged; }
    public void setLastChanged(long lastChanged) { this.lastChanged = lastChanged; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    public long getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(long lockedUntil) { this.lockedUntil = lockedUntil; }

    // ==================== HELPER METHODS ====================

    public boolean isLocked() {
        return System.currentTimeMillis() < lockedUntil;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
        // Khóa 5 phút nếu nhập sai 5 lần
        if (this.failedAttempts >= 5) {
            this.lockedUntil = System.currentTimeMillis() + (5 * 60 * 1000);
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.lockedUntil = 0;
    }
}