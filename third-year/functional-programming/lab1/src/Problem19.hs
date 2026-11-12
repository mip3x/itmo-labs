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

countSundaysGo :: Integer -> (Integer, Integer, Integer) -> Integer
countSundaysGo limitYear (year, month, weekday)
  | year > limitYear = 0
  | otherwise = contribution + countSundaysGo limitYear nextState
  where
    contribution = if weekday == 6 then 1 else 0
    nextState = nextMonthState (year, month, weekday)

countSundaysRecursive :: Integer
countSundaysRecursive = countSundaysGo 2000 (nextYearState 1901 (1900, 1, 0))

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

countSundaysComprehension :: Integer
countSundaysComprehension =
  foldr countOne 0 [x | x <- months, isSundayStart x]
  where
    months = monthsUpTo 2000 (nextYearState 1901 (1900, 1, 0))

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
