#include "dip.h"
#include "tm1637.h"

void initDipInputsPullup(void) {
    for (uint8_t i = 0; i < SWITCH_PINS; i++) {
        GPIO_TypeDef *port = DIP_Switch_8[i].port;
        uint8_t pin = DIP_Switch_8[i].pin;

        // MODER = 00 (вход)
        port->MODER &= ~(3 << (pin * 2));

        // port->PUPDR & ~(3 << (pin * 2)) -- очистка двух битов PUPDR
        // 1 << (pin * 2) -- PUPDR = 10 (Pull-down)
        port->PUPDR = (port->PUPDR & ~(3 << (pin * 2))) | (2 << (pin * 2));
    }
}

uint8_t readDipValue(void) {
    uint8_t value = 0;
    for (uint8_t i = 0; i < SWITCH_PINS; i++) {
        GPIO_TypeDef *port = DIP_Switch_8[i].port;
        uint8_t pin = DIP_Switch_8[i].pin;

        uint8_t bit = (port->IDR >> pin) & 1;
        value |= (bit << i);
    }
    return value;
}

void scanDip(void) {
    static uint8_t lastValue = 0;

    // TODO: переделать механику обработки lastScanTime - сейчас за неё отвечает клавиатура
    if (tickCount - lastScanTime >= 50) {
        uint8_t currentValue = readDipValue();
        if (currentValue != lastValue) {
            lastValue = currentValue;
            printf("DIP = %u\n", (unsigned)currentValue);
            tm1637_display_value(lastValue);
        }
    }
}

