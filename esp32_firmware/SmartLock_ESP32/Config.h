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

#endif
