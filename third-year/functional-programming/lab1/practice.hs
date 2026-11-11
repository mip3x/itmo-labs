double :: Integer -> Integer
double x = x * 2

add :: Integer -> Integer -> Integer
add x y = x + y

square :: Integer -> Integer
square x = x * x

nextFib :: Integer -> Integer -> Integer
nextFib a b = a + b

absolute :: Integer -> Integer
absolute x = if x >= 0 then x else -x

isEven :: Integer -> Bool
isEven x = mod x 2 == 0

evenOrZero :: Integer -> Integer
evenOrZero x = if isEven x then x else 0

sumTo :: Integer -> Integer
sumTo n = if n <= 0 then 0 else n + sumTo (n - 1)

sumTo2 :: Integer -> Integer
sumTo2 n
    | n <= 0    = 0
    | otherwise = n + sumTo2 (n - 1)

factorial :: Integer -> Integer
factorial x
    | x == 0    = 1
    | otherwise = x * factorial (x - 1)

factorialAcc :: Integer -> Integer -> Integer
factorialAcc n acc
    | n == 0    = acc
    | otherwise = factorialAcc (n - 1) (acc * n)

factorialTail :: Integer -> Integer
factorialTail n = factorialAcc n 1

sumToAcc :: Integer -> Integer -> Integer
sumToAcc n acc
    | n <= 0    = acc
    | otherwise = sumToAcc (n - 1) (acc + n)

sumToTail :: Integer -> Integer
sumToTail n = sumToAcc n 0

fibGo :: Integer -> Integer -> Integer -> Integer
fibGo steps a b
    | steps == 0 = a
    | otherwise  = fibGo (steps - 1) b (a + b)

fib :: Integer -> Integer
fib n = fibGo n 1 2

sumEvenFibGo :: Integer -> Integer -> Integer -> Integer
sumEvenFibGo limit a b
    | a > limit = 0
    | otherwise = evenOrZero a + sumEvenFibGo limit b (a + b)

sumEvenFib :: Integer -> Integer
sumEvenFib limit = sumEvenFibGo limit 1 2

sumEvenFibAcc :: Integer -> Integer -> Integer -> Integer -> Integer
sumEvenFibAcc limit a b acc
    | a > limit = acc
    | otherwise = sumEvenFibAcc limit b (a + b) (acc + evenOrZero a)

sumEvenFibTail :: Integer -> Integer
sumEvenFibTail limit = sumEvenFibAcc limit 1 2 0

countDown :: Integer -> [Integer]
countDown n
    | n <= 0    = []
    | otherwise = n : countDown (n - 1)

fibsUpToGo :: Integer -> Integer -> Integer -> [Integer]
fibsUpToGo limit a b
    | a > limit = []
    | otherwise = a : (fibsUpToGo limit b (a + b))

fibsUpTo :: Integer -> [Integer]
fibsUpTo limit = fibsUpToGo limit 1 2

sumEvenFibModular :: Integer -> Integer
sumEvenFibModular limit = foldr (+) 0 (filter isEven (fibsUpTo limit))

firstFibs :: Integer -> [Integer]
firstFibs n = map fib [0..(n - 1)]

fibsUpToMap :: Integer -> [Integer]
fibsUpToMap limit = takeWhile (<= limit) (map fib [0..])

sumEvenFibMap :: Integer -> Integer
sumEvenFibMap limit = foldr (+) 0 (filter isEven (fibsUpToMap limit))

sumEvenFibComprehension :: Integer -> Integer
sumEvenFibComprehension limit = foldr (+) 0 [x | x <- fibsUpTo limit, isEven x]

fibsFrom :: Integer -> Integer -> [Integer]
fibsFrom a b = a : fibsFrom b (a + b)

fibsInfinite :: [Integer]
fibsInfinite = fibsFrom 1 2

sumEvenFibInfinite :: Integer -> Integer
sumEvenFibInfinite limit = foldr (+) 0 (filter isEven (takeWhile (<= limit) fibsInfinite))

isLeapYear :: Integer -> Bool
isLeapYear year = (mod year 400 == 0) || ((mod year 4 == 0) && (mod year 100 /= 0))

daysInMonth :: Integer -> Integer -> Integer
daysInMonth year month
    | month == 2 = if isLeapYear year then 29 else 28
    | elem month [4, 6, 9, 11] = 30
    | otherwise = 31

nextMonthWeekday :: Integer -> Integer -> Integer -> Integer
nextMonthWeekday year month weekday = mod (weekday + daysInMonth year month) 7

nextMonth :: Integer -> Integer -> (Integer, Integer)
nextMonth year month
    | month == 12 = (year + 1, 1)
    | otherwise   = (year, month + 1)

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

countSundaysGo :: Integer -> (Integer, Integer, Integer) -> Integer
countSundaysGo limitYear (year, month, weekday)
    | year > limitYear = 0
    | otherwise = contribution + countSundaysGo limitYear nextState 
    where
        contribution = if weekday == 6 then 1 else 0
        nextState = nextMonthState (year, month, weekday)

countSundaysRecursive :: Integer
countSundaysRecursive = countSundaysGo 2000 (nextYearState 1901 (1900, 1, 0))

countSundaysAcc
    :: Integer
    -> (Integer, Integer, Integer)
    -> Integer
    -> Integer
countSundaysAcc limitYear (year, month, weekday) acc
    | year > limitYear = acc
    | otherwise = countSundaysAcc limitYear nextState (acc + contribution)
    where 
        contribution = if weekday == 6 then 1 else 0
        nextState = nextMonthState (year, month, weekday)

countSundaysTail :: Integer
countSundaysTail = countSundaysAcc 2000 (nextYearState 1901 (1900, 1, 0)) 0

monthsUpTo
    :: Integer
    -> (Integer, Integer, Integer)
    -> [(Integer, Integer, Integer)]
monthsUpTo limitYear state@(year, _, _)
    | year > limitYear = []
    | otherwise = state : (monthsUpTo limitYear (nextMonthState state))

isSundayStart :: (Integer, Integer, Integer) -> Bool
isSundayStart (_, _, weekday) = weekday == 6

countOne :: (Integer, Integer, Integer) -> Integer -> Integer
countOne _ acc = acc + 1

countSundaysModular :: Integer
countSundaysModular = foldr countOne 0 (filter isSundayStart (monthsUpTo 2000 (nextYearState 1901 (1900, 1, 0))))

monthsInfinite :: [(Integer, Integer, Integer)]
monthsInfinite = iterate nextMonthState (nextYearState 1901 (1900, 1, 0))

countSundaysInfinite :: Integer
countSundaysInfinite =
    foldr countOne 0
        (filter isSundayStart
            (takeWhile (\(year, _, _) -> year <= 2000) monthsInfinite)
        )

countSundaysComprehension :: Integer
countSundaysComprehension =
    foldr countOne 0 [x | x <- months, isSundayStart x]
    where
        months = monthsUpTo 2000 (nextYearState 1901 (1900, 1, 0))

monthAfter
    :: Integer
    -> (Integer, Integer, Integer)
    -> (Integer, Integer, Integer)
monthAfter n state
    | n == 0    = state
    | otherwise = monthAfter (n - 1) (nextMonthState state)

monthsMap :: [(Integer, Integer, Integer)]
monthsMap = 
    map stateAt [0..(monthCount - 1)]
    where
        startYear = 1901
        limitYear = 2000

        startState = nextYearState startYear (1900, 1, 0)
        monthCount = (limitYear - startYear + 1) * 12

        stateAt n = monthAfter n startState

countSundaysMap :: Integer
countSundaysMap = foldr countOne 0 (filter isSundayStart monthsMap)