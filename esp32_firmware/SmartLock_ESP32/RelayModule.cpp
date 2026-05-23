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
  updateDoorStatus("UNLOCKED"); // Gửi trạng thái lên App
  
  delay(5000); // Giữ trong 5 giây
  
  digitalWrite(RELAY_PIN, HIGH); // Đóng lại
  updateDoorStatus("LOCKED");   // Cập nhật lại trạng thái khóa
  
  Serial.println("Cua da duoc khoa lai.\n");
}

void accessDenied() {
  Serial.println("=> TU CHOI MO CUA!");
  digitalWrite(BUZZER_PIN, HIGH);
  delay(1000);
  digitalWrite(BUZZER_PIN, LOW);
}
