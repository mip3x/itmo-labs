# Лабораторная работа №1

Университет ИТМО

Дисциплина: "Функциональное программирование"

Тема: "Простые алгоритмы и встроенные структуры данных"

Язык программирования: Haskell

Выполнил: Малышев Михаил Александрович

Группа: P3411

## Цель

Освоить рекурсию, списки, функции высшего порядка и ленивые вычисления.
[ТЗ](TASK.md). Исходники: [Problem2.hs](src/Problem2.hs),
[Problem19.hs](src/Problem19.hs), [Python](python/solutions.py).
Ниже приведён полный код решений, включая вспомогательные функции.

## Задача №2. Чётные числа Фибоначчи

Найти сумму чётных членов последовательности `1, 2, 3, 5, …`, не превышающих
`4 000 000`. Ответ: **`4613732`**.

Переход между числами: `(a, b) -> (b, a + b)`.
`evenOrZero` возвращает чётное число либо `0`.

### Общие функции

```haskell
module Problem2 where

isEven :: Integer -> Bool
isEven = even

evenOrZero :: Integer -> Integer
evenOrZero x = if isEven x then x else 0

countDown :: Integer -> [Integer]
countDown n
  | n <= 0 = []
  | otherwise = n : countDown (n - 1)
```

`isEven` использует стандартную проверку чётности, а `evenOrZero` позволяет
добавлять в сумму только чётные значения. `countDown` -- учебный генератор
убывающего списка; в итоговом решении он не используется.

### Обычная рекурсия

```haskell
sumEvenFibGo :: Integer -> Integer -> Integer -> Integer
sumEvenFibGo limit a b
  | a > limit = 0
  | otherwise = evenOrZero a + sumEvenFibGo limit b (a + b)

sumEvenFib :: Integer -> Integer
sumEvenFib limit = sumEvenFibGo limit 1 2
```

`a` -- текущее число, `b` -- следующее. Если `a` превышает границу,
возвращается `0`; иначе его чётный вклад складывается с результатом обработки
следующей пары. Рекурсия нехвостовая, поскольку результат вызова участвует
в сложении. Обёртка задаёт начальные числа `1` и `2`.

### Хвостовая рекурсия

```haskell
sumEvenFibAcc :: Integer -> Integer -> Integer -> Integer -> Integer
sumEvenFibAcc limit a b acc
  | a > limit = acc
  | otherwise = sumEvenFibAcc limit b (a + b) (acc + evenOrZero a)

sumEvenFibTail :: Integer -> Integer
sumEvenFibTail limit = sumEvenFibAcc limit 1 2 0
```

`acc` хранит сумму уже обработанных чётных чисел. При переходе к следующей
паре в него добавляется текущий вклад. После достижения границы возвращается
`acc`; рекурсивный вызов хвостовой, так как его результат сразу возвращается.
Начальная сумма равна `0`.

### Генерация, фильтрация и свёртка

```haskell
fibsUpToGo :: Integer -> Integer -> Integer -> [Integer]
fibsUpToGo limit a b
  | a > limit = []
  | otherwise = a : fibsUpToGo limit b (a + b)

fibsUpTo :: Integer -> [Integer]
fibsUpTo limit = fibsUpToGo limit 1 2

sumEvenFibModular :: Integer -> Integer
sumEvenFibModular limit = foldr (+) 0 (filter isEven (fibsUpTo limit))
```

`fibsUpTo` строит список чисел до границы: оператор `:` добавляет текущий
элемент, а `[]` завершает список. Затем `filter isEven` оставляет чётные числа,
и `foldr (+) 0` складывает их с начальным значением `0`. Генерация, отбор
и суммирование выделены в отдельные этапы.

### Генерация через map

```haskell
fibGo :: Integer -> Integer -> Integer -> Integer
fibGo steps a b
  | steps == 0 = a
  | otherwise = fibGo (steps - 1) b (a + b)

fib :: Integer -> Integer
fib n = fibGo n 1 2

firstFibs :: Integer -> [Integer]
firstFibs n = map fib [0 .. (n - 1)]

fibsUpToMap :: Integer -> [Integer]
fibsUpToMap limit = takeWhile (<= limit) (map fib [0 ..])

sumEvenFibMap :: Integer -> Integer
sumEvenFibMap limit = foldr (+) 0 (filter isEven (fibsUpToMap limit))
```

`fib n` возвращает число на позиции `n`, причём позиции `0` соответствует `1`.
`map fib` преобразует номера в числа Фибоначчи; `firstFibs` берёт заданное
количество позиций, а `fibsUpToMap` ограничивает значения через `takeWhile`.
Каждое число вычисляется заново от начальной пары.

### Списковое включение

```haskell
sumEvenFibComprehension :: Integer -> Integer
sumEvenFibComprehension limit = foldr (+) 0 [x | x <- fibsUpTo limit, isEven x]
```

Списковое включение перебирает `fibsUpTo limit` и сохраняет `x`, только если
`isEven x` истинно. Оно заменяет явный вызов `filter`; суммирование по-прежнему
выполняется свёрткой.

### Бесконечный список

```haskell
fibsFrom :: Integer -> Integer -> [Integer]
fibsFrom a b = a : fibsFrom b (a + b)

fibsInfinite :: [Integer]
fibsInfinite = fibsFrom 1 2

sumEvenFibInfinite :: Integer -> Integer
sumEvenFibInfinite limit = foldr (+) 0 (filter isEven (takeWhile (<= limit) fibsInfinite))
```

`fibsFrom` строит неограниченную последовательность, продолжая вычисление
с предыдущей пары. Ленивость позволяет получать элементы по запросу.
`takeWhile` останавливается на первом числе больше границы, после чего
конечный список фильтруется и суммируется.

## Задача №19. Воскресенья в начале месяца

Посчитать первые числа месяцев, пришедшиеся на воскресенье, с 1 января 1901
по 31 декабря 2000 года. Известно, что 1 января 1900 года -- понедельник.
Ответ: **`171`**.

Состояние: `(год, месяц, день недели)`, где месяцы нумеруются `1..12`,
дни недели -- `0..6` от понедельника до воскресенья.

### Календарные правила

```haskell
module Problem19 where

isLeapYear :: Integer -> Bool
isLeapYear year = (mod year 400 == 0) || ((mod year 4 == 0) && (mod year 100 /= 0))

daysInMonth :: Integer -> Integer -> Integer
daysInMonth year month
  | month == 2 = if isLeapYear year then 29 else 28
  | month `elem` [4, 6, 9, 11] = 30
  | otherwise = 31

nextMonthWeekday :: Integer -> Integer -> Integer -> Integer
nextMonthWeekday year month weekday = mod (weekday + daysInMonth year month) 7

nextMonth :: Integer -> Integer -> (Integer, Integer)
nextMonth year month
  | month == 12 = (year + 1, 1)
  | otherwise = (year, month + 1)

nextMonthState :: (Integer, Integer, Integer) -> (Integer, Integer, Integer)
nextMonthState (year, month, weekday) =
  (nextYear, nextMonthNumber, nextWeekday)
  where
    (nextYear, nextMonthNumber) = nextMonth year month
    nextWeekday = nextMonthWeekday year month weekday

nextYearState :: Integer -> (Integer, Integer, Integer) -> (Integer, Integer, Integer)
nextYearState limitYear (year, month, weekday)
  | year >= limitYear = (year, month, weekday)
  | otherwise =
      nextYearState limitYear (nextMonthState (year, month, weekday))
```

`isLeapYear` учитывает исключение для столетий: 1900 год невисокосный,
а 2000 -- високосный. `daysInMonth` определяет длину месяца, на которую
сдвигается день недели; `mod 7` убирает полные недели.

`nextMonthState` объединяет переход даты и дня недели. После декабря
получается январь следующего года. `nextYearState` последовательно обрабатывает
месяцы от известного понедельника `(1900, 1, 0)` до `(1901, 1, 1)` -- вторника.
Таким образом, начало периода вычисляется из условия.

### Обычная рекурсия

```haskell
countSundaysGo :: Integer -> (Integer, Integer, Integer) -> Integer
countSundaysGo limitYear (year, month, weekday)
  | year > limitYear = 0
  | otherwise = contribution + countSundaysGo limitYear nextState
  where
    contribution = if weekday == 6 then 1 else 0
    nextState = nextMonthState (year, month, weekday)

countSundaysRecursive :: Integer
countSundaysRecursive = countSundaysGo 2000 (nextYearState 1901 (1900, 1, 0))
```

Каждый месяц даёт вклад `1`, если его первое число -- воскресенье, иначе `0`.
Вклад складывается с результатом обработки остальных месяцев, поэтому рекурсия
нехвостовая. Условие `year > limitYear` завершает подсчёт только после декабря
2000 года; начальное состояние обёртка вычисляет через `nextYearState`.

### Хвостовая рекурсия

```haskell
countSundaysAcc ::
  Integer ->
  (Integer, Integer, Integer) ->
  Integer ->
  Integer
countSundaysAcc limitYear (year, month, weekday) acc
  | year > limitYear = acc
  | otherwise = countSundaysAcc limitYear nextState (acc + contribution)
  where
    contribution = if weekday == 6 then 1 else 0
    nextState = nextMonthState (year, month, weekday)

countSundaysTail :: Integer
countSundaysTail = countSundaysAcc 2000 (nextYearState 1901 (1900, 1, 0)) 0
```

Число найденных воскресений накапливается в `acc`, начальное значение -- `0`.
Следующий вызов получает новый месяц и обновлённый счётчик. При выходе за границу
года возвращается накопленный ответ; дополнительных операций с результатом
рекурсивного вызова нет.

### Генерация, фильтрация и свёртка

```haskell
monthsUpTo ::
  Integer ->
  (Integer, Integer, Integer) ->
  [(Integer, Integer, Integer)]
monthsUpTo limitYear state@(year, _, _)
  | year > limitYear = []
  | otherwise = state : monthsUpTo limitYear (nextMonthState state)

isSundayStart :: (Integer, Integer, Integer) -> Bool
isSundayStart (_, _, weekday) = weekday == 6

countOne :: (Integer, Integer, Integer) -> Integer -> Integer
countOne _ acc = acc + 1

countSundaysModular :: Integer
countSundaysModular = foldr countOne 0 (filter isSundayStart (monthsUpTo 2000 (nextYearState 1901 (1900, 1, 0))))
```

`monthsUpTo` генерирует состояния всех месяцев периода. Образец
`state@(year, _, _)` сохраняет всю тройку и извлекает год для проверки границы.
`filter isSundayStart` оставляет воскресенья, а `foldr countOne 0` подсчитывает
их: `countOne` игнорирует содержимое тройки и увеличивает счётчик.

### Генерация через map

```haskell
monthAfter ::
  Integer ->
  (Integer, Integer, Integer) ->
  (Integer, Integer, Integer)
monthAfter n state
  | n == 0 = state
  | otherwise = monthAfter (n - 1) (nextMonthState state)

monthsMap :: [(Integer, Integer, Integer)]
monthsMap =
  map stateAt [0 .. (monthCount - 1)]
  where
    startYear = 1901
    limitYear = 2000

    startState = nextYearState startYear (1900, 1, 0)
    monthCount = (limitYear - startYear + 1) * 12

    stateAt n = monthAfter n startState

countSundaysMap :: Integer
countSundaysMap = foldr countOne 0 (filter isSundayStart monthsMap)
```

`monthAfter n` выполняет `n` переходов от начального состояния.
`map stateAt` превращает номера переходов в состояния месяцев: `0` соответствует
январю 1901 года. Количество месяцев вычисляется из границ периода, включая
оба года. Затем применяются тот же фильтр и свёртка. Для каждого номера
переходы повторяются от начала.

### Списковое включение

```haskell
countSundaysComprehension :: Integer
countSundaysComprehension =
  foldr countOne 0 [x | x <- months, isSundayStart x]
  where
    months = monthsUpTo 2000 (nextYearState 1901 (1900, 1, 0))
```

Списковое включение отбирает из `months` состояния, удовлетворяющие
`isSundayStart`. Локальное определение `where` задаёт исходный список периода,
а `foldr countOne 0` считает отобранные месяцы.

### Бесконечный список

```haskell
monthsInfinite :: [(Integer, Integer, Integer)]
monthsInfinite = iterate nextMonthState (nextYearState 1901 (1900, 1, 0))

countSundaysInfinite :: Integer
countSundaysInfinite =
  foldr
    countOne
    0
    ( filter
        isSundayStart
        (takeWhile (\(year, _, _) -> year <= 2000) monthsInfinite)
    )
```

`iterate` многократно применяет `nextMonthState`, начиная с января 1901 года.
`takeWhile` оставляет месяцы до конца 2000 года; лямбда извлекает из тройки
только год. Ограниченный список передаётся фильтру воскресений и свёртке.

## Сравнение с Python

В [python/solutions.py](python/solutions.py) обе задачи решены циклами
с накоплением результата в изменяемой переменной.

```python
"""Imperative solutions to Project Euler problems 2 and 19."""


def sum_even_fibonacci(limit: int) -> int:
    a, b = 1, 2
    total = 0
    while a <= limit:
        if a % 2 == 0:
            total += a
        a, b = b, a + b
    return total


def is_leap_year(year: int) -> bool:
    return year % 400 == 0 or (year % 4 == 0 and year % 100 != 0)


def days_in_month(year: int, month: int) -> int:
    if month == 2:
        return 29 if is_leap_year(year) else 28
    if month in (4, 6, 9, 11):
        return 30
    return 31


def count_sundays(start_year: int = 1901, end_year: int = 2000) -> int:
    if start_year < 1900 or end_year < start_year:
        raise ValueError("Expected 1900 <= start_year <= end_year")

    weekday = 0  # 1 January 1900 was a Monday; Sunday is 6.
    total = 0
    for year in range(1900, end_year + 1):
        for month in range(1, 13):
            if year >= start_year and weekday == 6:
                total += 1
            weekday = (weekday + days_in_month(year, month)) % 7
    return total


if __name__ == "__main__":
    print("Problem 2:")
    print(sum_even_fibonacci(4_000_000))
    print("Problem 19:")
    print(count_sundays())
```

Календарные функции используют те же правила, что Haskell. Цикл начинается
с 1900 года для вычисления начального дня недели; подсчёт -- с 1901 года.
Обе программы дают одинаковые ответы. В Python состояние обновляется
присваиванием, в Haskell -- передаётся в следующий вызов или обрабатывается
функциями `map`, `filter`, `foldr`.

## Запуск и тесты

Команды выполняются из `lab1`:

```sh
cabal build all --enable-tests
cabal run lab1
cabal test --test-show-details=direct
python3 python/solutions.py
python3 -m unittest discover -s python -v
```

[Тесты Haskell](test/Main.hs) проверяют все варианты, границы Фибоначчи,
високосные годы, длины месяцев, переход года и границы периода.
[Тесты Python](python/test_solutions.py) дополнительно сверяют каждый год
1900--2000 с календарём `datetime`.

```sh
ormolu --mode inplace src/Problem2.hs src/Problem19.hs app/Main.hs test/Main.hs
ormolu --mode check src/Problem2.hs src/Problem19.hs app/Main.hs test/Main.hs
hlint src app test
```

## Выводы

Обе задачи решены шестью способами на Haskell и циклами на Python.
Обычная рекурсия объединяет результат с текущим вкладом, хвостовая передаёт
накопленный ответ. В Haskell хвостовая форма не гарантирует постоянной памяти:
из-за ленивости в аккумуляторе могут накапливаться отложенные вычисления.

Разделение генерации, фильтрации и свёртки упрощает повторное использование
функций. Списковые включения сокращают запись отбора. В текущих вариантах
`map` повторяет переходы от начала для каждого элемента, поэтому число переходов
квадратичное; последовательные генераторы и циклы делают линейное число переходов.
Замеры производительности не проводились.

Ленивый список -- неизменяемая структура с вычислением элементов по запросу;
вычисленные элементы могут переиспользоваться при сохранении ссылки.
Итератор продвигает состояние при получении следующего элемента; генератор Python
для повторного прохода нужно создать заново. `takeWhile` завершает список при
первом нарушении условия, а `filter` продолжает поиск, поэтому для подсчёта
по бесконечному списку сначала необходимо ограничить период.
