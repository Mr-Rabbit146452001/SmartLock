#include "RelayModule.h"
#include "Config.h"
#include "FirebaseModule.h" // Thêm thư viện để cập nhật trạng thái lên Firebase

void Relay_Init() {
  pinMode(RELAY_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(RELAY_PIN, HIGH); // Mặc định khóa (Active LOW)
  digitalWrite(BUZZER_PIN, LOW); // Tắt còi
}

void openDoor(String method) {
  Serial.println("=> MO CUA THANG CONG BANG: " + method);
  
  digitalWrite(BUZZER_PIN, HIGH);
  delay(150);
  digitalWrite(BUZZER_PIN, LOW);
  
  digitalWrite(RELAY_PIN, LOW); // Kích relay mở chốt
  updateDoorStatus(false);      // Gửi trạng thái MỞ (isLocked = false) lên Firebase
  
  delay(5000); // Giữ trong 5 giây
  
  digitalWrite(RELAY_PIN, HIGH); // Đóng lại
  updateDoorStatus(true);        // Gửi trạng thái KHÓA (isLocked = true) lên Firebase
  
  Serial.println("Cua da duoc khoa lai.\n");
}

void lockDoor() {
  digitalWrite(RELAY_PIN, HIGH); // Tắt relay (khóa chốt)
  updateDoorStatus(true);        // Cập nhật trạng thái KHÓA lên Firebase
  Serial.println("=> CUA DA DUOC KHOA LAP TUC.");
}

void accessDenied() {
  Serial.println("=> TU CHOI MO CUA!");
  digitalWrite(BUZZER_PIN, HIGH);
  delay(1000);
  digitalWrite(BUZZER_PIN, LOW);
}
