package com.example.smartlock.utils;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionHelper {

    private static final String AES_ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA-256";

    // Hash PIN (SHA-256)
    public static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Mã hóa dữ liệu
    public static String encrypt(String data, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Giải mã dữ liệu
    public static String decrypt(String encryptedData, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Tạo key đơn giản từ PIN
    public static String generateKeyFromPin(String pin) {
        return hashPin(pin).substring(0, 16); // Lấy 16 ký tự đầu làm key
    }
}