#include "FirebaseModule.h"
#include <WiFi.h>
#include <Firebase_ESP_Client.h>

#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#include "Config.h"
#include "RelayModule.h"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

bool signupOK = false;

void Firebase_Init() {
  Serial.print("Ket noi WiFi: ");
  Serial.println(WIFI_SSID);
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  // Nếu muốn code không bị kẹt khi mất WiFi, có thể dùng bộ đếm
  int retry = 0;
  while (WiFi.status() != WL_CONNECTED && retry < 20) {
    delay(500);
    Serial.print(".");
    retry++;
  }
  
  if(WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi Connected.");
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());

    /* Cấu hình Firebase */
    config.api_key = FIREBASE_API_KEY;
    config.database_url = FIREBASE_URL;

    /* Xác thực (Ẩn danh) */
    if (Firebase.signUp(&config, &auth, "", "")) {
      Serial.println("Firebase Auth thanh cong");
      signupOK = true;
    } else {
      Serial.printf("Loi Auth: %s\n", config.signer.signupError.message.c_str());
    }

    config.token_status_callback = tokenStatusCallback; 

    Firebase.begin(&config, &auth);
    Firebase.reconnectWiFi(true);
  } else {
    Serial.println("\nLoi: Khong the ket noi WiFi.");
  }
}

void checkFirebaseCommand() {
  if (Firebase.ready() && signupOK) {
    // Đọc trạng thái từ nhánh SmartLock/Command/Unlock
    if (Firebase.RTDB.getString(&fbdo, "SmartLock/Command/Unlock")) {
      if (fbdo.dataType() == "string") {
        String command = fbdo.stringData();
        if (command == "FACE_UNLOCK" || command == "APP_UNLOCK") {
          // Xóa lệnh sau khi đọc (tránh lặp lại lệnh cũ)
          Firebase.RTDB.setString(&fbdo, "SmartLock/Command/Unlock", "IDLE");
          
          openDoor("Mo cua tu App (Firebase: " + command + ")");
        }
      }
    }
  }
}

void updateDoorStatus(String status) {
  if (Firebase.ready() && signupOK) {
    Firebase.RTDB.setString(&fbdo, "SmartLock/Status/Door", status);
  }
}
