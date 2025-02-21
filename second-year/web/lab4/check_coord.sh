#!/bin/bash

# Базовый URL для API
BASE_URL="http://localhost:8080/api/plot/check"

echo "Тестовый случай 0: 0 0 4"
response=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"x": 0, "y": 0, "radius": 4.0}')
echo "Ответ: $response"

# Тестовый случай 1: Валидный ввод
echo "Тестовый случай 1: Валидный ввод"
response=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"x": 1.5, "y": 0.5, "radius": 3.0}')
echo "Ответ: $response"

# Тестовый случай 2: Пропущено значение x
echo "\nТестовый случай 2: Пропущено значение x"
response=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"y": 2.5, "radius": 3.0}')
echo "Ответ: $response"

# Тестовый случай 3: Отрицательный радиус
echo "\nТестовый случай 3: Отрицательный радиус"
response=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"x": 1.5, "y": 2.5, "radius": -3.0}')
echo "Ответ: $response"

# Тестовый случай 4: Пропущены все значения
echo "\nТестовый случай 4: Пропущены все значения"
response=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{}')
echo "Ответ: $response"

# Тестовый случай 5: Валидный ввод с большими числами
echo "\nТестовый случай 5: Валидный ввод с большими числами"
response=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"x": 1000.5, "y": 2000.5, "radius": 500.0}')
echo "Ответ: $response"

# Конец скрипта
echo "\nТесты завершены."

