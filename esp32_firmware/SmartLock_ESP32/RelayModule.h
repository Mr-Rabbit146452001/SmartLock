#ifndef RELAY_MODULE_H
#define RELAY_MODULE_H
#include <Arduino.h>

void Relay_Init();
void openDoor(String method);
void lockDoor();
void accessDenied();

#endif
