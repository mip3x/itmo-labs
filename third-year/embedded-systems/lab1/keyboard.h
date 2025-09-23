#ifndef KEYBOARD_H
#define KEYBOARD_H

#include <stdio.h>
#include <stdint.h>
#include <string.h>

#include "main.h"

void initKeyboard();
char readKey();
void scanKeyboard();

#define BIN_BUTTON 'B'
#define DEC_BUTTON 'D'
#define HEX_BUTTON 'H'

extern char lastKey;
extern uint32_t lastScanTime;

#endif