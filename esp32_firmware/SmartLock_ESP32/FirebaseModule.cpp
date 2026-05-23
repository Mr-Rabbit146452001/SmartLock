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
    // 1. Kiểm tra lệnh MỞ KHÓA (command/unlock)
    if (Firebase.RTDB.getBool(&fbdo, "command/unlock")) {
      if (fbdo.dataType() == "boolean" && fbdo.boolData() == true) {
        // Reset lại lệnh về false để tránh lặp lệnh
        Firebase.RTDB.setBool(&fbdo, "command/unlock", false);
        openDoor("App Android (Firebase Unlock)");
      }
    }

    // 2. Kiểm tra lệnh KHÓA CỬA LẬP TỨC (command/lock)
    if (Firebase.RTDB.getBool(&fbdo, "command/lock")) {
      if (fbdo.dataType() == "boolean" && fbdo.boolData() == true) {
        // Reset lại lệnh về false
        Firebase.RTDB.setBool(&fbdo, "command/lock", false);
        lockDoor();
      }
    }
  }
}

void updateDoorStatus(bool isLocked) {
  if (Firebase.ready() && signupOK) {
    // Cập nhật trạng thái khóa dạng Boolean lên nhánh door/isLocked
    Firebase.RTDB.setBool(&fbdo, "door/isLocked", isLocked);
  }
}
