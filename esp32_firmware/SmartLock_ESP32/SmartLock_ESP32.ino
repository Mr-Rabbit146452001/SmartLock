#include <Arduino.h>
#include "Config.h"
#include "RelayModule.h"
#include "KeypadModule.h"
#include "FingerprintModule.h"
#include "BluetoothModule.h"

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("\n--- He thong SmartLock khoi dong ---");
  
  Relay_Init();
  Keypad_Init();
  Fingerprint_Init();
  Bluetooth_Init();
}

void loop() {
  checkKeypad();
  checkFingerprint();
  checkAppCommand();
}
