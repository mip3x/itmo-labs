#ifndef DIP_H
#define DIP_H

#include <stdio.h>
#include <stdint.h>
#include <string.h>

#include "main.h"

void initDipInputsPullup(void);
uint8_t readDipValue(void);
void scanDip(void);

typedef struct {
    GPIO_TypeDef *port;
    uint8_t pin;
} GPIO_Pin;

#define SWITCH_PINS 8

static const GPIO_Pin DIP_Switch_8[SWITCH_PINS] = {
    { GPIOA, 12 },
    { GPIOA, 11 },
    { GPIOB, 1 },
    { GPIOA, 4 },
    { GPIOA, 1 },
    { GPIOA, 0 },
    { GPIOC, 14 },
    { GPIOC, 13 },
};

extern uint32_t lastScanTime;

#endif