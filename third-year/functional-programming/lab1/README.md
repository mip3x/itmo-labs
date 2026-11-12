# Лабораторная работа №1

Университет ИТМО

Дисциплина: "Функциональное программирование"

Тема: "Простые алгоритмы и встроенные структуры данных"

Язык программирования: Haskell

Выполнил: Малышев Михаил Александрович

Группа: P3411

Санкт-Петербург, 2026

## Цель работы

Освоить функции, сопоставление с образцом, обычную и хвостовую рекурсию,
списки, функции высшего порядка, отображение, фильтрацию, свёртку и ленивые
вычисления на примере задач проекта Эйлера №2 и №19.

[Исходное задание](TASK.md).

## Задача №2. Сумма чётных чисел Фибоначчи

Последовательность начинается с `1` и `2`; каждое следующее число равно сумме
двух предыдущих: `1, 2, 3, 5, 8, 13, …`. Требуется найти сумму чётных членов,
значения которых не превышают `4 000 000`. Граница включается в рассмотрение.

Для перехода между числами используется пара `(a, b)`: текущий член и следующий.
Новая пара имеет вид `(b, a + b)`. Функция `evenOrZero` возвращает текущее число,
если оно чётное, иначе `0`.

### Обычная рекурсия

```haskell
sumEvenFibGo :: Integer -> Integer -> Integer -> Integer
sumEvenFibGo limit a b
  | a > limit = 0
  | otherwise = evenOrZero a + sumEvenFibGo limit b (a + b)

sumEvenFib :: Integer -> Integer
sumEvenFib limit = sumEvenFibGo limit 1 2
```

К результату рекурсивного вызова прибавляется вклад текущего числа.
Рекурсия не хвостовая, поскольку результат участвует в сложении.
Условие `a > limit` завершает обработку.

### Хвостовая рекурсия

```haskell
sumEvenFibAcc :: Integer -> Integer -> Integer -> Integer -> Integer
sumEvenFibAcc limit a b acc
  | a > limit = acc
  | otherwise = sumEvenFibAcc limit b (a + b) (acc + evenOrZero a)

sumEvenFibTail :: Integer -> Integer
sumEvenFibTail limit = sumEvenFibAcc limit 1 2 0
```

Аргумент `acc` хранит накопленную сумму. Результат рекурсивного вызова
сразу возвращается вызывающей стороне. Отдельный список чисел не строится.

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

`fibsUpTo` генерирует числа в пределах границы, `filter isEven` оставляет чётные,
а `foldr (+) 0` складывает их. Этапы обработки явно разделены.

### Генерация через map

```haskell
fibsUpToMap :: Integer -> [Integer]
fibsUpToMap limit = takeWhile (<= limit) (map fib [0 ..])

sumEvenFibMap :: Integer -> Integer
sumEvenFibMap limit = foldr (+) 0 (filter isEven (fibsUpToMap limit))
```

Функция `fib` вычисляет число по позиции, начиная с позиции `0`, которой
соответствует число `1`. `map` преобразует позиции в числа Фибоначчи.
`takeWhile` завершает последовательность на первом числе больше границы.
Каждый вызов `fib` заново проходит последовательность от начальной пары.

### Списковое включение

```haskell
sumEvenFibComprehension :: Integer -> Integer
sumEvenFibComprehension limit = foldr (+) 0 [x | x <- fibsUpTo limit, isEven x]
```

Синтаксис `[x | x <- ..., условие]` задаёт перебор и отбор элементов.
Полученный список чётных чисел передаётся свёртке.

### Бесконечный список

```haskell
fibsFrom :: Integer -> Integer -> [Integer]
fibsFrom a b = a : fibsFrom b (a + b)

fibsInfinite :: [Integer]
fibsInfinite = fibsFrom 1 2

sumEvenFibInfinite :: Integer -> Integer
sumEvenFibInfinite limit = foldr (+) 0 (filter isEven (takeWhile (<= limit) fibsInfinite))
```

`fibsFrom` не имеет условия остановки, но благодаря ленивости элементы
вычисляются по мере необходимости. В отличие от варианта с `map fib`,
переход продолжается с предыдущей пары. `takeWhile` ограничивает список
перед фильтрацией и суммированием.

**Результат задачи №2: `4613732`.**

## Задача №19. Воскресенья в начале месяца

Требуется посчитать, сколько раз первое число месяца приходилось на воскресенье
в период с 1 января 1901 года по 31 декабря 2000 года включительно.
Известно, что 1 января 1900 года было понедельником.

Год високосный, если делится на `400` либо делится на `4`, но не на `100`.
В феврале високосного года 29 дней, обычного -- 28. В апреле, июне, сентябре
и ноябре 30 дней, в остальных месяцах -- 31.

### Представление состояния и календарные правила

Состояние месяца -- тройка `(year, month, weekday)`, содержащая год,
номер месяца `1..12` и день недели его первого числа. Используется нумерация
`0 = понедельник, …, 6 = воскресенье`.

```haskell
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
```

Первое число следующего месяца сдвигается на длину текущего месяца по модулю `7`.
После декабря номер месяца становится `1`, а год увеличивается на единицу.

Начальное состояние 1901 года вычисляется из данных условия:

```haskell
nextYearState :: Integer -> (Integer, Integer, Integer) -> (Integer, Integer, Integer)
nextYearState limitYear (year, month, weekday)
  | year >= limitYear = (year, month, weekday)
  | otherwise =
      nextYearState limitYear (nextMonthState (year, month, weekday))
```

Вычисление `nextYearState 1901 (1900, 1, 0)` даёт `(1901, 1, 1)` -- вторник.
В этой работе функция вызывается с январём исходного года, поэтому возвращает
январь целевого года. В общем случае она останавливается на первом состоянии,
год которого не меньше заданного.

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

Вклад месяца равен `1`, если он начинается с воскресенья, иначе `0`.
Этот вклад прибавляется к результату обработки следующих месяцев.
Условие `year > limitYear` включает декабрь последнего года в подсчёт.

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

Число найденных воскресений передаётся через `acc`.
После рекурсивного вызова дополнительных операций с его результатом нет.

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


Генератор строит список состояний месяцев. Фильтр оставляет месяцы,
начинающиеся с воскресенья. Свёртка считает элементы: значение самой тройки
в `countOne` не используется. Явная свёртка сохранена для демонстрации приёма;
в прикладном коде для такого подсчёта также подходит `length`.

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
Количество месяцев вычисляется из границ периода: `(2000 - 1901 + 1) * 12`.
`map` сопоставляет каждому номеру перехода состояние месяца. Период здесь
задан полными годами. Для каждого номера переходы повторяются от начала.

### Списковое включение

```haskell
countSundaysComprehension :: Integer
countSundaysComprehension =
  foldr countOne 0 [x | x <- months, isSundayStart x]
  where
    months = monthsUpTo 2000 (nextYearState 1901 (1900, 1, 0))
```


Списковое включение отбирает подходящие состояния из конечного списка месяцев,
после чего свёртка подсчитывает их количество.

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


`iterate f start` строит список `start, f start, f (f start), …`.
Функция `nextMonthState` задаёт переход, а `takeWhile` ограничивает период
концом 2000 года. Отдельного вычисления количества месяцев здесь не требуется.

**Результат задачи №19: `171`.**

## Особенности Haskell

### Функции как значения

`map`, `filter`, `foldr` и `iterate` принимают функции аргументами.
В тестах решения также хранятся в списке вместе с названиями, что позволяет
применять один набор проверок к нескольким реализациям.
Сопоставление с образцом разбирает состояние месяца на компоненты;
`state@(year, _, _)` одновременно сохраняет всю тройку и извлекает год.

### Ленивость и хвостовая рекурсия

Хвостовая форма описывает положение рекурсивного вызова, но сама по себе
не гарантирует постоянный расход памяти в Haskell. Выражение `acc + contribution`
может оставаться отложенным и образовывать цепочку вычислений. В текущих
реализациях аккумулятор явно не форсируется. Для управления строгостью можно
использовать `seq`, строгие образцы или `foldl'` для свёртки слева.

`foldr` объединяет элементы справа: например, сумма списка `[2, 8, 34]`
имеет вид `2 + (8 + (34 + 0))`. Для получения суммы нужен весь конечный список.

### Ленивый список и итератор

Ленивый список Haskell — неизменяемая структура данных, части которой вычисляются
по запросу. Вычисленные элементы могут переиспользоваться, если список остаётся
доступен по общей ссылке. Это также может удерживать уже вычисленные элементы
в памяти.

Итератор обычно представляет процесс последовательного получения элементов:
операция получения следующего значения продвигает его состояние. Повторный
проход, например для генератора Python, требует создания нового генератора.
Ленивость сама по себе не делает список бесконечным: бесконечность задаётся
определением генератора.

`takeWhile` завершает список при первом нарушении условия. `filter` продолжает
искать подходящие элементы. Поэтому замена `takeWhile` на `filter` при
ограничении бесконечного календаря годом не позволит завершить подсчёт:
после 2000 года поиск продолжится бесконечно.

### Сравнение способов

| Способ | Особенность |
| --- | --- |
| Обычная рекурсия | Объединяет текущий вклад с результатом обработки остатка |
| Хвостовая рекурсия | Передаёт накопленный результат следующему вызову |
| Модульный вариант | Разделяет генерацию, фильтрацию и свёртку |
| `map` | Генерирует элементы по номерам; текущие реализации повторяют переходы от начала |
| Списковое включение | Компактно выражает перебор и условие отбора |
| Бесконечный список | Отделяет неограниченную генерацию от ограничения периода |

Если обработать `N` чисел или месяцев, последовательный генератор делает
линейное число переходов. Текущие генераторы через `map fib` и `map monthAfter`
выполняют суммарно квадратичное число переходов, поскольку для каждого номера
заново проходят путь от начала. Это оценка числа переходов без учёта стоимости
арифметики над `Integer`; замеры времени в работе не проводились.

## Сборка и запуск

Команды выполняются из директории `lab1`.

Окружение, закреплённое в CI:

| Инструмент | Версия |
| --- | --- |
| GHC | 9.6.7 |
| cabal-install | 3.12.1.0 |
| HLint | 3.8 |
| Ormolu | 0.7.4.0 |

Сборка и запуск:

```sh
cabal build all --enable-tests
cabal run lab1
```

Вывод программы после сборки:

```text
Problem 2:
4613732
Problem 19:
171
```

Интерактивная работа:

```sh
cabal repl
```

В GHCi модули можно подключить через `import Problem2` и `import Problem19`,
а тип функции посмотреть командой `:t`.

## Тестирование

Тестовая программа использует `assertEqual` и завершает работу с ненулевым кодом
при первом несовпадении. Это позволяет CI обнаружить неуспешную проверку.

```sh
cabal test --test-show-details=direct
```

Проверяются:

- Все шесть вариантов задачи №2 на границах `0`, `1`, `2`, `8`, `10`, `100`
  и `4 000 000`. Граница `8` отдельно проверяет включение чётного числа,
  равного пределу.
- Все шесть вариантов задачи №19 на ожидаемом результате `171`.
- Правило високосности для 1900, 2000, 2024 и 2023 годов.
- Длина февраля обычного и високосного года, апреля и января.
- Переход декабря в январь и вычисление начального состояния 1901 года.
- Включение последнего месяца периода и исключение следующего года.
- Количество месяцев в периоде: 1200.

Всего предусмотрено 61 сравнение: 42 для задачи №2, 6 для результатов задачи №19
и 13 для календарных правил и границ. Cabal запускает их как одну тестовую
программу. Механизм ошибки дополнительно проверялся временной заменой ожидаемого
ответа `171` на `172`: тест завершился неуспешно; затем ожидаемый ответ восстановлен.

## Форматирование, линтер и CI

Исходники форматируются Ormolu:

```sh
ormolu --mode inplace src/Problem2.hs src/Problem19.hs app/Main.hs test/Main.hs
```

Проверка форматирования и линтера:

```sh
ormolu --mode check src/Problem2.hs src/Problem19.hs app/Main.hs test/Main.hs
hlint src app test
```

Для подсказки HLint `Use sum` заданы исключения только в четырёх функциях
задачи №2: в них необходимо явно показать `foldr`. Остальные подсказки
продолжают проверяться.

[Workflow GitHub Actions](../../../.github/workflows/lab1.yml) выполняет установку
инструментов, проверку форматирования, HLint, сборку, тестирование и запуск
программы. Запуск настроен для изменений лабораторной и самого workflow
при push и pull request, а также вручную.
[История запусков CI](https://github.com/mip3x/itmo-labs/actions/workflows/lab1.yml).

Для библиотеки, программы и тестов в Cabal включены `-Wall -Werror`:
предупреждения компилятора приводят к ошибке сборки.
`dist-newstyle/` и другие артефакты сборки исключены через `.gitignore`.

Фрагменты Haskell в этом отчёте взяты из соответствующих исходных файлов
и оформлены тем же форматтером. Текущий workflow проверяет исходники;
автоматическая проверка фрагментов Markdown в нём пока не настроена.

## Сравнение с традиционным языком

Реализации на традиционном языке пока отсутствуют. Для выполнения этого пункта
ТЗ необходимо добавить решения обеих задач, их запуск и сравнение с Haskell.
Планируемый язык сравнения — Python. Этот раздел следует дополнить после
реализации, не подменяя практическое сравнение теоретическим описанием.

## Выводы

Обе задачи решены шестью способами на Haskell. Рекурсивные варианты позволяют
явно описать переход между состояниями; аккумулятор переносит накопленный
результат в аргументы следующего вызова. Модульный подход делает этапы обработки
последовательности видимыми и переиспользуемыми. Списковые включения сокращают
запись отбора, а бесконечные списки позволяют задавать генерацию независимо
от границ конкретной задачи.

Работа с Haskell показала, что хвостовая рекурсия и строгость вычислений — разные
свойства. При выборе реализации важно учитывать повторные вычисления и время
жизни ленивых списков. Тесты проверяют не только итоговые ответы, но и календарные
границы; линтер и форматтер задают единые требования к оформлению кода.
