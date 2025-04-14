#!/bin/bash

set -euo pipefail

rm -rf .git
rm -f .gitignore
rm -rf src

# инициализация репозитория
git init

# установка nvim редактором по умолчанию для решения конфликтов слияния
echo -e "\n[merge]\n\ttool = nvim" >> .git/config

# внесение в конфиг данных о 1ом (красном) пользователе
git config user.name "red"
git config user.email "red@user.ru"

# проверка, верен ли конфиг
cat .git/config

echo "commits" >> .gitignore
echo "git.sh" >> .gitignore
echo "docs" >> .gitignore
echo "README.md" >> .gitignore

git add .gitignore
git commit --author="init <init@user.ru>" -m "chore: .gitignore added"

git checkout -b branch1

# ревизия r0 (пользователь 1) {
unzip commits/commit0.zip -d src
git add .
git commit -m "r0"
# }

# r1-r2 (1) {
# -o: флаг перезаписи (overwrite)

unzip -o commits/commit1.zip -d src
git add .
git commit -m "r1"

unzip -o commits/commit2.zip -d src
git add .
git commit -m "r2"
# }

# r3-r4 (2) {
git checkout -b branch2

unzip -o commits/commit3.zip -d src
git add .
git commit --author="blue <blue@user.ru>" -m "r3"

unzip -o commits/commit4.zip -d src
git add .
git commit --author="blue <blue@user.ru>" -m "r4"
# }

# r5 (1) {
git checkout -b branch3

unzip -o commits/commit5.zip -d src
git add .
git commit -m "r5"
# }

# r6-r7 (2)
git checkout branch2

unzip -o commits/commit6.zip -d src
git add .
git commit --author="blue <blue@user.ru>" -m "r6"

unzip -o commits/commit7.zip -d src
git add .
git commit --author="blue <blue@user.ru>" -m "r7"
# }

# r8 (1) {
git checkout branch1

unzip -o commits/commit8.zip -d src
git add .
git commit -m "r8"
# }

# r9 (1) {
git checkout branch3

unzip -o commits/commit9.zip -d src
git add .
git commit -m "r9"
# }

# r10 (1) {
git checkout branch1

unzip -o commits/commit10.zip -d src
git add .
git commit -m "r10"
# }

# r11 (1) {
git checkout branch3

unzip -o commits/commit11.zip -d src
git add .
git commit -m "r11"
# }

# слияние branch1 и branch3: r12 (1) {
git checkout branch1

git merge --no-commit branch3
git commit -m "r12"
# }

# r13 (2) {
git checkout branch2

unzip -o commits/commit13.zip -d src
git add .
git commit --author="blue <blue@user.ru>" -m "r13"
# }

# r14 (1) {
git checkout branch1
unzip -o commits/commit14.zip -d src

git merge --no-commit branch2

git add .
git commit -m "r14"
# }

# перенос указателя branch2 на указатель branch1
# git branch -f branch2 branch1

# граф коммитов
git log --graph --oneline
