#!/bin/bash

BASE_URL="http://localhost:8080/api/auth"

echo "===> Регистрация нового пользователя..."
curl -X POST "$BASE_URL/register" \
-H "Content-Type: application/json" \
-d '{
  "username": "new_user",
  "password": "secure_password"
}'
echo -e "\n"

echo "===> Авторизация зарегистрированного пользователя..."
curl -X POST "$BASE_URL/login" \
-H "Content-Type: application/json" \
-d '{
  "username": "new_user",
  "password": "secure_password"
}'
echo -e "\n"

echo "===> Регистрация пользователя с тем же именем (ожидается ошибка)..."
curl -X POST "$BASE_URL/register" \
-H "Content-Type: application/json" \
-d '{
  "username": "new_user",
  "password": "another_password"
}'
echo -e "\n"

echo "===> Попытка авторизации с неверными данными..."
curl -X POST "$BASE_URL/login" \
-H "Content-Type: application/json" \
-d '{
  "username": "wrong_user",
  "password": "wrong_password"
}'
echo -e "\n"

echo "Все запросы выполнены."

