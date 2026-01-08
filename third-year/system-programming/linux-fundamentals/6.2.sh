#!/bin/bash
read -r a b # Читаем переменные, переданные на стандартный ввод

sum=0
for ((i=a; i<=b; i++))
do
  let sum+=i
done

echo $sum
