#include "KeypadModule.h"
#include <Keypad.h>
#include "Config.h"
#include "RelayModule.h"

const byte ROWS = 4;
const byte COLS = 4;
char keys[ROWS][COLS] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
byte rowPins[ROWS] = {13, 12, 14, 27}; 
byte colPins[COLS] = {26, 25, 33, 32}; 

Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);

String inputPassword = "";
String correctPassword = "1234";

void Keypad_Init() {
    inputPassword = "";
}

void checkKeypad() {
  char key = keypad.getKey();
  if (key) {
    digitalWrite(BUZZER_PIN, HIGH);
    delay(50);
    digitalWrite(BUZZER_PIN, LOW);

    if (key == '#') { 
      if (inputPassword == correctPassword) {
        openDoor("Mat khau ban phim");
      } else {
        accessDenied();
      }
      inputPassword = ""; 
    } 
    else if (key == '*') { 
      inputPassword = "";
      Serial.println("Da xoa mat khau vua nhap");
    } 
    else {
      inputPassword += key;
      Serial.print("*"); 
    }
  }
}
