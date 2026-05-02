package com.example.smartlock.utils;

import android.content.Context;
import com.example.smartlock.database.AppDatabase;
import com.example.smartlock.models.LockHistory;
import com.example.smartlock.models.MasterPin;
import com.example.smartlock.models.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PinManager {

    private static PinManager instance;
    private Context context;
    private AppDatabase database;
    private ExecutorService executorService;

    private PinManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized PinManager getInstance(Context context) {
        if (instance == null) {
            instance = new PinManager(context);
        }
        return instance;
    }

    // Kiểm tra đã có mã PIN chưa
    public boolean hasMasterPin() {
        MasterPin masterPin = database.masterPinDao().getMasterPin();
        return masterPin != null && masterPin.getPinHash() != null;
    }

    // Tạo mã PIN lần đầu (chỉ Owner)
    public boolean createMasterPin(String pin, User owner) {
        if (!owner.isOwner()) {
            return false;
        }

        String pinHash = EncryptionHelper.hashPin(pin);
        String encryptedPin = EncryptionHelper.encrypt(pin,
                EncryptionHelper.generateKeyFromPin(pin));

        MasterPin masterPin = new MasterPin(encryptedPin, pinHash);
        masterPin.setChangedBy(owner.getUserId());

        database.masterPinDao().insertMasterPin(masterPin);
        return true;
    }

    // Xác thực mã PIN
    public boolean verifyPin(String inputPin) {
        MasterPin masterPin = database.masterPinDao().getMasterPin();

        if (masterPin == null) {
            return false;
        }

        // Kiểm tra khóa
        if (masterPin.isLocked()) {
            return false;
        }

        String inputHash = EncryptionHelper.hashPin(inputPin);

        if (inputHash.equals(masterPin.getPinHash())) {
            masterPin.resetFailedAttempts();
            database.masterPinDao().updateMasterPin(masterPin);
            return true;
        } else {
            masterPin.incrementFailedAttempts();
            database.masterPinDao().updateMasterPin(masterPin);
            return false;
        }
    }

    // Đổi mã PIN (chỉ Owner)
    public boolean changeMasterPin(String oldPin, String newPin, User owner) {
        if (!owner.isOwner()) {
            return false;
        }

        if (!verifyPin(oldPin)) {
            return false;
        }

        String newHash = EncryptionHelper.hashPin(newPin);
        String newEncryptedPin = EncryptionHelper.encrypt(newPin,
                EncryptionHelper.generateKeyFromPin(newPin));

        MasterPin masterPin = database.masterPinDao().getMasterPin();
        masterPin.setEncryptedPin(newEncryptedPin);
        masterPin.setPinHash(newHash);
        masterPin.setChangedBy(owner.getUserId());
        masterPin.setLastChanged(System.currentTimeMillis());
        masterPin.resetFailedAttempts();

        database.masterPinDao().updateMasterPin(masterPin);

        // Ghi log
        addToHistory(owner.getUserId(), owner.getFullName(), "pin_changed", true);

        return true;
    }

    // Xác thực để mở khóa
    public boolean authenticateForUnlock(String pin, User user) {
        boolean verified = verifyPin(pin);
        addToHistory(user.getUserId(), user.getFullName(),
                "unlock", verified ? "pin" : "pin_failed", verified);
        return verified;
    }

    // Lấy thông báo lỗi khóa
    public String getLockMessage() {
        MasterPin masterPin = database.masterPinDao().getMasterPin();
        if (masterPin != null && masterPin.isLocked()) {
            long remaining = (masterPin.getLockedUntil() - System.currentTimeMillis()) / 1000;
            long minutes = remaining / 60;
            long seconds = remaining % 60;
            return "Mã PIN bị khóa. Vui lòng thử lại sau " + minutes + " phút " + seconds + " giây";
        }
        return null;
    }

    // Ghi log
    private void addToHistory(String userId, String userName, String action, boolean success) {
        addToHistory(userId, userName, action, "system", success);
    }
    public MasterPin getMasterPin() {
        return database.masterPinDao().getMasterPin();
    }

    private void addToHistory(String userId, String userName, String action, String method, boolean success) {
        executorService.execute(() -> {
            LockHistory history = new LockHistory(userId, userName, action, method, success);
            database.lockHistoryDao().insertHistory(history);
        });
    }
}