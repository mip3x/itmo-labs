#include "keyboard.h"

void initKeyboard() {
    // Настройка строк (R1-R4) как выходы с открытым стоком
    // PB7, PB6, PB3, PA10
    GPIOB->MODER = (GPIOB->MODER & ~(3U << (7 * 2))) | (1U << (7 * 2));
    GPIOB->MODER = (GPIOB->MODER & ~(3U << (6 * 2))) | (1U << (6 * 2));
    GPIOB->MODER = (GPIOB->MODER & ~(3U << (3 * 2))) | (1U << (3 * 2));
    GPIOA->MODER = (GPIOA->MODER & ~(3U << (10 * 2))) | (1U << (10 * 2));

    GPIOB->OTYPER |= (1 << 7) | (1 << 6) | (1 << 3);
    GPIOA->OTYPER |= (1 << 10);

    // Настройка столбцов (C1-C3) как входы с подтяжкой к питанию
    // PB10, PB4, PB5
    GPIOB->MODER &= ~(3U << (10 * 2) | 3U << (4 * 2) | 3U << (5 * 2));
    GPIOB->PUPDR = (GPIOB->PUPDR & ~(3U << (10 * 2))) | (1U << (10 * 2));
    GPIOB->PUPDR = (GPIOB->PUPDR & ~(3U << (4 * 2))) | (1U << (4 * 2));
    GPIOB->PUPDR = (GPIOB->PUPDR & ~(3U << (5 * 2))) | (1U << (5 * 2));

    lastKey = '\0';
    lastScanTime = 0;
}

char readKey() {
    const uint8_t rows[] = {7, 6, 10, 3}; // Пины строк
    const uint8_t cols[] = {10, 4, 5};    // Пины столбцов
    const char keymap[4][3] = {
        {'B', 'D', 'H'}, {'?', '?', '?'}, {'?', '?', '?'}, {'?', '?', '?'}};

    for (uint8_t i = 0; i < 4; i++) {
        // Активируем текущую строку
        if (rows[i] != 10) {
            GPIOB->BSRR = (1 << (rows[i] + 16));
        } else {
            GPIOA->BSRR = (1 << (rows[i] + 16));
        }

        // Небольшая задержка для стабилизации
        for (volatile int d = 0; d < 100; d++)
            ;

        // Проверяем столбцы
        for (uint8_t j = 0; j < 3; j++) {
            if ((GPIOB->IDR & (1 << cols[j])) == 0) {
                // Деактивируем строку перед возвратом
                if (rows[i] != 10) {
                    GPIOB->BSRR = (1 << rows[i]);
                } else {
                    GPIOA->BSRR = (1 << rows[i]);
                }
                return keymap[i][j];
            }
        }

        // Деактивируем строку
        if (rows[i] != 10) {
            GPIOB->BSRR = (1 << rows[i]);
        } else {
            GPIOA->BSRR = (1 << rows[i]);
        }
    }

    return '\0';
}

void scanKeyboard(void) {
    // Сканируем клавиатуру каждые 50мс
    if (tickCount - lastScanTime >= 50) {
        lastScanTime = tickCount;
        char currentKey = readKey();

        if (currentKey != '\0' && currentKey != lastKey) {
            printf("Pressed: %c\n", currentKey);
            lastKey = currentKey;

            if (lastKey == BIN_BUTTON)
                set_mode(BIN_MODE);
            else if (lastKey == DEC_BUTTON)
                set_mode(DEC_MODE);
            else if (lastKey == HEX_BUTTON)
                set_mode(HEX_MODE);

        } else if (currentKey == '\0') {
            lastKey = '\0';
        }
    }
}