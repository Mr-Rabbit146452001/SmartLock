package com.example.smartlock.models;

public class Device {
    private String deviceId;
    private String deviceName;
    private String deviceSerial;
    private String pinHash;
    private boolean isOnline;
    private long lastSeen;

    public Device() {} // Constructor rỗng cho Firebase

    public Device(String deviceId, String deviceName, String deviceSerial, String pinHash) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceSerial = deviceSerial;
        this.pinHash = pinHash;
        this.isOnline = false;
        this.lastSeen = System.currentTimeMillis();
    }

    // Getters và Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceSerial() { return deviceSerial; }
    public void setDeviceSerial(String deviceSerial) { this.deviceSerial = deviceSerial; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public long getLastSeen() { return lastSeen; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }
}