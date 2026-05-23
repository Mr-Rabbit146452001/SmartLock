#include "FingerprintModule.h"
#include <Adafruit_Fingerprint.h>
#include "Config.h"
#include "RelayModule.h"

#define mySerial Serial2 
Adafruit_Fingerprint finger = Adafruit_Fingerprint(&mySerial);

void Fingerprint_Init() {
  mySerial.begin(57600, SERIAL_8N1, FINGERPRINT_RX, FINGERPRINT_TX);
  if (finger.verifyPassword()) {
    Serial.println("Da tim thay cam bien van tay!");
  } else {
    Serial.println("Loi: Khong tim thay cam bien van tay!");
  }
}

void checkFingerprint() {
  uint8_t p = finger.getImage();
  if (p != FINGERPRINT_OK) return;

  p = finger.image2Tz();
  if (p != FINGERPRINT_OK) return;

  p = finger.fingerSearch();
  if (p == FINGERPRINT_OK) {
    openDoor("Van tay (ID: " + String(finger.fingerID) + ")");
  } else {
    accessDenied();
  }
}
