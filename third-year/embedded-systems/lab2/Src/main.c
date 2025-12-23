/* USER CODE BEGIN Header */
/**
 ******************************************************************************
 * @file           : main.c
 * @brief          : Main program body
 ******************************************************************************
 * @attention
 *
 * <h2><center>&copy; Copyright (c) 2019 STMicroelectronics.
 * All rights reserved.</center></h2>
 *
 * This software component is licensed by ST under BSD 3-Clause license,
 * the "License"; You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *                        opensource.org/licenses/BSD-3-Clause
 *
 ******************************************************************************
 */
/* USER CODE END Header */

/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "gpio.h"
#include "i2c.h"
#include "usart.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include "fonts.h"
#include "kb.h"
#include "oled.h"
#include "pca9538.h"
#include "sdk_uart.h"
#include <stdlib.h>
#include <string.h>
#include "tim.h"
#include "buzzer.h"
/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */

/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/

/* USER CODE BEGIN PV */
/* Megalovania melody to play when countdown ends */
static uint32_t megalovania_melody[] = {
    N_D3, N_D3, N_D4, N_A3, 0, N_GS3, N_G3, N_F3, N_D3, N_F3, N_G3,
    N_C3, N_C3, N_D4, N_A3, 0, N_GS3, N_G3, N_F3, N_D3, N_F3, N_G3,
    N_B2, N_B2, N_D4, N_A3, 0, N_GS3, N_G3, N_F3, N_D3, N_F3, N_G3,
    N_AS2, N_AS2, N_D4, N_A3, 0, N_GS3, N_G3, N_F3, N_D3, N_F3, N_G3
};

static uint32_t megalovania_delays[] = {
    16, 16, 8, 6, 32, 8, 8, 8, 16, 16, 16,
    16, 16, 8, 6, 32, 8, 8, 8, 16, 16, 16,
    16, 16, 8, 6, 32, 8, 8, 8, 16, 16, 16,
    16, 16, 8, 6, 32, 8, 8, 8, 16, 16, 16
};


static uint32_t imperial_march_melody[] = {
    N_G4, N_G4, N_G4, N_DS4, N_AS4, N_G4, N_DS4, N_AS4, N_G4,
    N_D5, N_D5, N_D5, N_DS5, N_AS4, N_FS4, N_DS4, N_AS4, N_G4,
    N_G5, N_G4, N_G4, N_G5, N_FS5, N_F5, N_E5, N_DS5,
    N_E5, 0,    N_GS4, N_CS5, N_C5, N_B4, N_AS4, N_AS4, N_G4,
    N_G4, N_AS4, N_D5, N_G4, N_AS4, N_D5, N_G4
};

static uint32_t imperial_march_delays[] = {
    8, 8, 8, 6, 16, 8, 6, 16, 32,
    8, 8, 8, 6, 16, 8, 6, 16, 32,
    8, 6, 16, 8, 8, 8, 8, 16,
    8, 8, 8, 8, 8, 8, 16, 8, 32,
    8, 8, 16, 8, 8, 16, 24
};

static uint32_t tetris_melody[] = {
    N_E5, N_B4, N_C5, N_D5, N_C5, N_B4, N_A4, N_A4,
    N_C5, N_E5, N_D5, N_C5, N_B4,
    N_C5, N_D5, N_E5, N_C5, N_A4, N_A4,

    N_D5, N_F5, N_A5, N_G5, N_F5, N_E5, N_C5, N_E5,
    N_D5, N_C5, N_B4, N_B4,
    N_C5, N_D5, N_E5, N_C5, N_A4, N_A4
};

static uint32_t tetris_delays[] = {
    /* 8 ≈ восьмая, 16 ≈ четверть (подгони под свой Buzzer_Play, если нужно) */
     8,  8,  8,  8,  8,  8, 16, 16,
     8,  8,  8,  8,  8,
     8,  8,  8,  8,  8, 16,

     8,  8, 16,  8,  8,  8,  8,  8,
     8,  8,  8, 16,
     8,  8, 16,  8,  8, 16
};


/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
/* USER CODE BEGIN PFP */
void KB_Test(void);
void OLED_KB(uint8_t OLED_Keys[]);
void oled_Reset(void);
void KB_InputMode(void);

void ShowWelcomeScreen(void);
void WaitForStartKey(void);
/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */

/* USER CODE END 0 */

/**
 * @brief  The application entry point.
 * @retval int
 */
int main(void) {
  /* USER CODE BEGIN 1 */

  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick.
   */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_I2C1_Init();
  MX_USART6_UART_Init();
  /* USER CODE BEGIN 2 */
  MX_TIM2_Init();
  Buzzer_Init();
  oled_Init();

  /* USER CODE END 2 */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1) {

    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
    /* Enter numeric input mode: blocks here and updates OLED as digits are
     * entered */
	ShowWelcomeScreen();

	WaitForStartKey();

    KB_InputMode();
  }
  /* USER CODE END 3 */
}

/**
 * @brief System Clock Configuration
 * @retval None
 */
void SystemClock_Config(void) {
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  /** Configure the main internal regulator output voltage
   */
  __HAL_RCC_PWR_CLK_ENABLE();
  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE1);
  /** Initializes the CPU, AHB and APB busses clocks
   */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSE;
  RCC_OscInitStruct.HSEState = RCC_HSE_ON;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLM = 25;
  RCC_OscInitStruct.PLL.PLLN = 336;
  RCC_OscInitStruct.PLL.PLLP = RCC_PLLP_DIV2;
  RCC_OscInitStruct.PLL.PLLQ = 4;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK) {
    Error_Handler();
  }
  /** Initializes the CPU, AHB and APB busses clocks
   */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK | RCC_CLOCKTYPE_SYSCLK |
                                RCC_CLOCKTYPE_PCLK1 | RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV4;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV2;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_5) != HAL_OK) {
    Error_Handler();
  }
}

/* USER CODE BEGIN 4 */
/*
 * u64_to_decstr: Convert unsigned long long to decimal string.
 * Avoids snprintf %llu which may not be supported on all embedded toolchains.
 */
static void u64_to_decstr(unsigned long long v, char *buf, size_t bufsize) {
  if (bufsize == 0)
    return;
  if (v == 0ULL) {
    if (bufsize > 1) {
      buf[0] = '0';
      buf[1] = '\0';
    } else {
      buf[0] = '\0';
    }
    return;
  }
  char tmp[32];
  int ti = 0;
  while (v > 0ULL && ti < (int)sizeof(tmp) - 1) {
    tmp[ti++] = (char)('0' + (v % 10ULL));
    v /= 10ULL;
  }
  /* ensure we don't overflow buf */
  size_t need = (size_t)ti + 1;
  if (need > bufsize) {
    /* truncate most-significant digits if necessary */
    int keep = (int)bufsize - 1;
    if (keep <= 0) {
      buf[0] = '\0';
      return;
    }
    for (int i = 0; i < keep; ++i) {
      buf[i] = tmp[keep - 1 - i];
    }
    buf[keep] = '\0';
    return;
  }
  /* reverse tmp into buf */
  for (int i = 0; i < ti; ++i) {
    buf[i] = tmp[ti - 1 - i];
  }
  buf[ti] = '\0';
}

void KB_Test(void) {
  UART_Transmit((uint8_t *)"KB test start\n");
  uint8_t R = 0, C = 0, L = 0, Row[4] = {ROW4, ROW3, ROW2, ROW1}, Key, OldKey,
          OLED_Keys[12] = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                           0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
  oled_Reset();
  oled_WriteString("From bottom to top", Font_7x10, White);
  OLED_KB(OLED_Keys);
  oled_UpdateScreen();
  for (int i = 0; i < 4; i++) {
    while (!(R && C && L)) {
      OldKey = Key;
      Key = Check_Row(Row[i]);
      if (Key == 0x01 && Key != OldKey) {
        UART_Transmit((uint8_t *)"Right pressed\n");
        R = 1;
        OLED_Keys[2 + 3 * i] = 0x31;
        OLED_KB(OLED_Keys);
      } else if (Key == 0x02 && Key != OldKey) {
        UART_Transmit((uint8_t *)"Center pressed\n");
        C = 1;
        OLED_Keys[1 + 3 * i] = 0x31;
        OLED_KB(OLED_Keys);
      } else if (Key == 0x04 && Key != OldKey) {
        UART_Transmit((uint8_t *)"Left pressed\n");
        L = 1;
        OLED_Keys[3 * i] = 0x31;
        OLED_KB(OLED_Keys);
      }
    }
    UART_Transmit((uint8_t *)"Row complete\n");
    R = C = L = 0;
    HAL_Delay(25);
  }
  UART_Transmit((uint8_t *)"KB test complete\n");
}

void OLED_KB(uint8_t OLED_Keys[12]) {
  for (int i = 3; i >= 0; i--) {
    oled_SetCursor(56, 5 + (4 - i) * 10);
    for (int j = 0; j < 3; j++) {
      oled_WriteChar(OLED_Keys[j + 3 * i], Font_7x10, White);
    }
  }
  oled_UpdateScreen();
}

void oled_Reset(void) {
  oled_Fill(Black);
  oled_SetCursor(0, 0);
  oled_UpdateScreen();
}

/*
 * KB_InputMode:
 * - Reads keypad presses, maps physical row/col to characters
 * - Appends numeric digits (0-9) to a buffer and displays on the OLED
 * - Replaces initial single '0' when a non-zero digit is entered
 * - '#' key starts countdown from current number
 * - '*' key resets to "0"
 */
void KB_InputMode(void) {
  char number[17];
  int len = 1;
  /* initial number is "0" */
  number[0] = '0';
  number[1] = '\0';

  uint8_t RowArr[4] = {ROW4, ROW3, ROW2,
                       ROW1}; /* scan bottom->top (matches existing code) */
  const char keymap[4][3] = {
      {'*', '0', '#'}, /* bottom row */
      {'7', '8', '9'},
      {'4', '5', '6'},
      {'1', '2', '3'} /* top row */
  };

  /* show initial value */
  oled_Reset();
  oled_SetCursor(0, 24);
  oled_WriteString(number, Font_11x18, White);
  oled_UpdateScreen();

  for (;;) {
    for (int i = 0; i < 4; i++) {
      uint8_t Key = Check_Row(RowArr[i]);
      if (Key != 0x00) {
        int col = -1;
        if (Key == 0x04)
          col = 0; /* left */
        else if (Key == 0x02)
          col = 1; /* center */
        else if (Key == 0x01)
          col = 2; /* right */

        if (col >= 0) {
          char ch = keymap[i][col];
          /* reset on star key */
          if (ch == '*') {
            number[0] = '0';
            number[1] = '\0';
            len = 1;
            /* update OLED to show reset */
            oled_Reset();
            oled_SetCursor(0, 24);
            oled_WriteString(number, Font_11x18, White);
            oled_UpdateScreen();
          }
          /* start countdown on hash key */
          else if (ch == '#') {
            unsigned long long val = strtoull(number, NULL, 10);
            /* perform countdown, update OLED every second */
            while (val > 0ULL) {
              val--;
              /* convert val back to string using safe helper */
              u64_to_decstr(val, number, sizeof(number));
              len = (int)strlen(number);
              oled_Reset();
              oled_SetCursor(0, 24);
              oled_WriteString(number, Font_11x18, White);
              oled_UpdateScreen();
              HAL_Delay(1000);
            }
            /* ensure display shows 0 */
            number[0] = '0';
            number[1] = '\0';
            len = 1;
            oled_Reset();
            oled_SetCursor(0, 24);
            oled_WriteString(number, Font_11x18, White);
            oled_UpdateScreen();
            /* Play Megalovania melody when countdown ends */
            Buzzer_Play(megalovania_melody, megalovania_delays, sizeof(megalovania_melody)/sizeof(uint32_t));
//            Buzzer_Play(imperial_march_melody, imperial_march_delays, sizeof(megalovania_melody)/sizeof(uint32_t));
            //Buzzer_Play(tetris_melody, tetris_delays, sizeof(megalovania_melody)/sizeof(uint32_t));
            return;
          }
          /* only accept digits 0-9 */
          else if (ch >= '0' && ch <= '9') {
            /* handle leading zero: if current is single '0' and new digit !=
             * '0', replace */
            if (len == 1 && number[0] == '0') {
              if (ch != '0') {
                number[0] = ch;
                number[1] = '\0';
                len = 1;
              }
              /* if ch == '0' and number is "0", keep as single zero (no change)
               */
            } else {
              if (len < (int)(sizeof(number) - 1)) {
                number[len] = ch;
                len++;
                number[len] = '\0';
              }
            }

            /* update OLED */
            oled_Reset();
            oled_SetCursor(0, 24);
            oled_WriteString(number, Font_11x18, White);
            oled_UpdateScreen();
          }

          /* simple debounce & wait-for-release */
          HAL_Delay(50);
          while (Check_Row(RowArr[i]) != 0x00) {
            HAL_Delay(20);
          }
          HAL_Delay(100);
        }
      }
      HAL_Delay(10);
    }
  }
}

void WaitForStartKey(void) {
  uint8_t RowArr[4] = {ROW4, ROW3, ROW2, ROW1};

  while (1) {
    for (int i = 0; i < 4; i++) {
      uint8_t key = Check_Row(RowArr[i]);
      if (key != 0x00) {
        // простейший дебаунс
        HAL_Delay(50);
        // дождаться отпускания
        while (Check_Row(RowArr[i]) != 0x00) {
          HAL_Delay(20);
        }
        HAL_Delay(100);
        return; // выходим, идём в KB_InputMode()
      }
    }
    HAL_Delay(10);
  }
}

void ShowWelcomeScreen(void) {
  oled_Reset();

  // Заголовок
  oled_SetCursor(0, 0);
  oled_WriteString("TIMER", Font_7x10, White);

  // Небольшое описание
  oled_SetCursor(0, 12);
  oled_WriteString("Keypad layout:", Font_7x10, White);

  // ASCII-арт 3x3 (цифры 1..9)
  oled_SetCursor(0, 26);
  oled_WriteString("[1] [2] [3]", Font_7x10, White);

  oled_SetCursor(0, 38);
  oled_WriteString("[4] [5] [6]", Font_7x10, White);

  oled_SetCursor(0, 50);
  oled_WriteString("[7] [8] [9]", Font_7x10, White);

  // Подсказка, как запустить программу таймера
  // (у тебя в KB_InputMode запуск отсчёта по '#')
  // Здесь мы говорим: "Нажмите любую клавишу, чтобы перейти к вводу,
  // а потом #, чтобы стартовать."
  // Если места мало по ширине — можно сократить текст.
  // Например:
  // "Press any key" и "then # to start"
  //
  oled_SetCursor(70, 0);
  oled_WriteString("Press",   Font_7x10, White);
  oled_SetCursor(70, 10);
  oled_WriteString("any key", Font_7x10, White);
  oled_SetCursor(70, 20);
  oled_WriteString("then #",  Font_7x10, White);
  oled_SetCursor(70, 30);
  oled_WriteString("to start",Font_7x10, White);

  oled_UpdateScreen();
}

/* USER CODE END 4 */

/**
 * @brief  This function is executed in case of error occurrence.
 * @retval None
 */
void Error_Handler(void) {
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */

  /* USER CODE END Error_Handler_Debug */
}

#ifdef USE_FULL_ASSERT
/**
 * @brief  Reports the name of the source file and the source line number
 *         where the assert_param error has occurred.
 * @param  file: pointer to the source file name
 * @param  line: assert_param error line source number
 * @retval None
 */
void assert_failed(uint8_t *file, uint32_t line) {
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line
     number, tex: printf("Wrong parameters value: file %s on line %d\r\n", file,
     line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
