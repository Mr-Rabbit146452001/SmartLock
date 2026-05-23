#include "BluetoothModule.h"
#include <BluetoothSerial.h>
#include "RelayModule.h"

BluetoothSerial SerialBT;

void Bluetooth_Init() {
  SerialBT.begin("SmartLock_ESP32"); 
  Serial.println("Bluetooth da bat. Doi ket noi tu App Android...");
}

void checkAppCommand() {
  if (SerialBT.available()) {
    String command = SerialBT.readStringUntil('\n');
    command.trim(); 
    
    if (command == "FACE_UNLOCK") {
      openDoor("Nhan dien khuon mat (Tu App Android)");
    }
    else if (command == "APP_UNLOCK") {
      openDoor("Mo cua tu nut bam App Android");
    }
  }
}
