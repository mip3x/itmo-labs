module Problem2 where

isEven :: Integer -> Bool
isEven = even

evenOrZero :: Integer -> Integer
evenOrZero x = if isEven x then x else 0

fibGo :: Integer -> Integer -> Integer -> Integer
fibGo steps a b
  | steps == 0 = a
  | otherwise = fibGo (steps - 1) b (a + b)

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
  | n <= 0 = []
  | otherwise = n : countDown (n - 1)

fibsUpToGo :: Integer -> Integer -> Integer -> [Integer]
fibsUpToGo limit a b
  | a > limit = []
  | otherwise = a : fibsUpToGo limit b (a + b)

fibsUpTo :: Integer -> [Integer]
fibsUpTo limit = fibsUpToGo limit 1 2

sumEvenFibModular :: Integer -> Integer
sumEvenFibModular limit = foldr (+) 0 (filter isEven (fibsUpTo limit))

firstFibs :: Integer -> [Integer]
firstFibs n = map fib [0 .. (n - 1)]

fibsUpToMap :: Integer -> [Integer]
fibsUpToMap limit = takeWhile (<= limit) (map fib [0 ..])

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
