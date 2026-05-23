#ifndef FIREBASE_MODULE_H
#define FIREBASE_MODULE_H

#include <Arduino.h>

void Firebase_Init();
void checkFirebaseCommand();
void updateDoorStatus(String status);

#endif
