package com.example.smartlock.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;  // ← Thêm import này

@Entity(tableName = "users")
public class User implements Serializable {  // ← Thêm implements Serializable

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatarPath;
    private String role;

    private String fingerprintTemplate;
    private String faceEmbedding;

    private boolean isActive;
    private long createdAt;
    private long lastLogin;

    public User() {}

    public User(String fullName, String phoneNumber, String email, String role) {
        this.userId = java.util.UUID.randomUUID().toString();
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFingerprintTemplate() { return fingerprintTemplate; }
    public void setFingerprintTemplate(String fingerprintTemplate) {
        this.fingerprintTemplate = fingerprintTemplate;
    }

    public String getFaceEmbedding() { return faceEmbedding; }
    public void setFaceEmbedding(String faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }

    // ==================== HELPER METHODS ====================

    public boolean hasFingerprint() {
        return fingerprintTemplate != null && !fingerprintTemplate.isEmpty();
    }

    public boolean hasFace() {
        return faceEmbedding != null && !faceEmbedding.isEmpty();
    }

    public boolean isOwner() {
        return "owner".equalsIgnoreCase(role);
    }

    public boolean isMember() {
        return "member".equalsIgnoreCase(role);
    }
}