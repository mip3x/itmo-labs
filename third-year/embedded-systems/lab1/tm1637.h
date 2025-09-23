#ifndef TM1637_H
#define TM1637_H

#include <stdio.h>
#include <stdint.h>
#include <string.h>

#include "main.h"

// TM1637 defines
#define TM1637_CLK_PIN 6 // PA6
#define TM1637_DIO_PIN 7 // PA7

#define TM1637_CMD1 0x40 // Данные команда: автоматическое увеличение адреса
#define TM1637_CMD2 0xC0 // Адрес команда: установка начального адреса
#define TM1637_CMD3 0x8F // Управление дисплеем: включить, максимальная яркость

#define SEG_H 0x76 // H = b c e f g
#define SEG_L 0x38 // L = d e f

// Прототипы функций для TM1637
void tm1637_init(void);
void tm1637_start(void);
void tm1637_stop(void);
void tm1637_write_byte(uint8_t byte);
void tm1637_display_digit(uint8_t digit, uint8_t data);
void tm1637_display_dec(int number);
void tm1637_display_hex(uint8_t number);
void tm1637_display_bin(uint8_t ch, uint8_t level); 
void tm1637_display_value(int value);
void tm1637_clear(void);
void delay_us(uint32_t us);
void tm1637_update(void);

extern uint32_t lastDisplayUpdate;
extern uint16_t counter;

#endif