#ifndef CONFIG_H
#define CONFIG_H

// ================= CẤU HÌNH CHÂN (GPIO) =================
const int RELAY_PIN = 18;  
const int BUZZER_PIN = 23; 

// ================= CẤU HÌNH BÀN PHÍM =================
const int ROW_PINS[] = {13, 12, 14, 27}; 
const int COL_PINS[] = {26, 25, 33, 32}; 

// ================= CẤU HÌNH VÂN TAY =================
const int FINGERPRINT_RX = 16;
const int FINGERPRINT_TX = 17;

// ================= CẤU HÌNH WIFI =================
#define WIFI_SSID "YOUR_WIFI_SSID"
#define WIFI_PASSWORD "YOUR_WIFI_PASSWORD"

// ================= CẤU HÌNH FIREBASE =================
#define FIREBASE_URL "https://smartlockapp-23e26-default-rtdb.firebaseio.com/"
#define FIREBASE_API_KEY "AIzaSyAXbgEGW0XOwkoZMnSEw7O0VZkD6fW1yX8"

#endif
