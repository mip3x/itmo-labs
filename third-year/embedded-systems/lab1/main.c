#include "main.h"
#include "tm1637.h"

volatile uint32_t tickCount;
uint32_t lastDisplayUpdate;
uint16_t counter;
char lastKey;
uint32_t lastScanTime;
volatile display_mode_t displayMode;

void osSystickHandler(void) { tickCount++; }

void initGPIO() {
    // Включаем тактирование GPIOA, GPIOB и GPIOC
    RCC->AHBENR |= RCC_AHBENR_GPIOAEN | RCC_AHBENR_GPIOBEN | RCC_AHBENR_GPIOCEN;

    // Настраиваем PA5 как выход
    GPIOA->MODER = (GPIOA->MODER & ~(3 << (5 * 2))) | (1 << (5 * 2));
    GPIOA->OTYPER &= ~(1 << 5);
    GPIOA->OSPEEDR |= (1 << 10);

    // PA9 (DEC)
    GPIOA->MODER = (GPIOA->MODER & ~(3 << (DEC_LED * 2)))  | (1 << (DEC_LED * 2));
    GPIOA->OTYPER &= ~(1 << DEC_LED);

    // PA15 (BIN)
    GPIOA->MODER = (GPIOA->MODER & ~(3 << (BIN_LED * 2))) | (1 << (BIN_LED * 2));
    GPIOA->OTYPER &= ~(1 << BIN_LED);

    // PC7 (HEX)
    GPIOC->MODER = (GPIOC->MODER & ~(3 << (HEX_LED * 2))) | (1 << (HEX_LED * 2));
    GPIOC->OTYPER &= ~(1 << HEX_LED);
}

void initUSART2() {
    // Включаем тактирование USART2
    RCC->APB1ENR |= RCC_APB1ENR_USART2EN;

    // Настраиваем PA2 и PA3 в альтернативный режим
    GPIOA->MODER = (GPIOA->MODER & ~(0xF << 4)) | (0xA << 4);
    GPIOA->AFR[0] = (GPIOA->AFR[0] & ~(0xFF << 8)) | (1 << 8) | (1 << 12);

    // Настраиваем USART2
    USART2->BRR = 417; // 48MHz/115200
    USART2->CR1 = USART_CR1_TE | USART_CR1_UE;
}

void initSysTick() {
    SysTick->LOAD = 47999; // 1ms при 48MHz
    SysTick->VAL = 0;
    SysTick->CTRL = (1 << 2) | (1 << 1) | (1 << 0);
}

int _write(int file, uint8_t *ptr, int len) {
    for (int i = 0; i < len; i++) {
        while (!(USART2->ISR & USART_ISR_TXE))
            ;
        USART2->TDR = ptr[i];
    }
    return len;
}

void checkTickCount() {
    static uint32_t lastTick = 0;
    if (tickCount - lastTick >= 2000) {
        lastTick = tickCount;
        GPIOA->ODR ^= (1 << 5); // Toggle LED
        printf("tickCount = %d!\n", tickCount);
    }
}

static inline void leds_off(void) {
    // + 16 becase of RR (Reset Register)
    uint8_t resetOffset = 16;
    GPIOA->BSRR = (1 << (DEC_LED + resetOffset))
                | (1 << (BIN_LED + resetOffset));
    GPIOC->BSRR = (1 << (HEX_LED + resetOffset));
}

void set_mode(display_mode_t mode) {
    displayMode = mode;
    leds_off();

    if (mode == BIN_MODE)
        GPIOA->BSRR = (1 << BIN_LED);
    else if (mode == DEC_MODE)
        GPIOA->BSRR = (1 << DEC_LED);
    else if (mode == HEX_MODE)
        GPIOC->BSRR = (1 << HEX_LED);
}

int main(void) {
    initGPIO();
    initUSART2();
    initSysTick();
    initKeyboard();
    tm1637_init();

    printf("Hello, %s!\n", "Wokwi Simulation");

    // Включаем LED
    GPIOA->ODR = (1 << 5);
    set_mode(DEC_MODE);

    while (1) {
        checkTickCount();
        tm1637_update();
        scanKeyboard();
    }

    return 0;
}