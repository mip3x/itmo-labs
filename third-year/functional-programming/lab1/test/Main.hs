module Main where

import Control.Monad (forM_, unless)
import qualified Problem19
import qualified Problem2
import System.Exit (exitFailure)

assertEqual :: (Eq a, Show a) => String -> a -> a -> IO ()
assertEqual label expected actual =
  unless (expected == actual) $ do
    putStrLn ("FAIL: " ++ label)
    putStrLn ("Expected: " ++ show expected)
    putStrLn ("Actual: " ++ show actual)
    exitFailure

main :: IO ()
main = do
  forM_ fibonacciSolutions $ \(name, solve) ->
    forM_ fibonacciCases $ \(limit, expected) ->
      assertEqual
        (name ++ ", limit = " ++ show limit)
        expected
        (solve limit)

  forM_ sundaySolutions $ \(name, result) ->
    assertEqual ("Sunday count: " ++ name) 171 result

  assertEqual "1900 is not leap" False (Problem19.isLeapYear 1900)
  assertEqual "2000 is leap" True (Problem19.isLeapYear 2000)
  assertEqual "2024 is leap" True (Problem19.isLeapYear 2024)
  assertEqual "2023 is not leap" False (Problem19.isLeapYear 2023)

  assertEqual "February 1900" 28 (Problem19.daysInMonth 1900 2)
  assertEqual "February 2000" 29 (Problem19.daysInMonth 2000 2)
  assertEqual "April" 30 (Problem19.daysInMonth 1901 4)
  assertEqual "January" 31 (Problem19.daysInMonth 1901 1)

  assertEqual
    "December to January"
    (1901, 1)
    (Problem19.nextMonth 1900 12)

  assertEqual
    "Start of 1901"
    (1901, 1, 1)
    (Problem19.nextYearState 1901 (1900, 1, 0))

  assertEqual
    "Include December of final year"
    1
    (Problem19.countSundaysGo 1901 (1901, 12, 6))

  assertEqual
    "Exclude months after final year"
    0
    (Problem19.countSundaysGo 1901 (1902, 1, 2))

  assertEqual
    "Number of months"
    1200
    ( length
        ( Problem19.monthsUpTo
            2000
            (Problem19.nextYearState 1901 (1900, 1, 0))
        )
    )

  putStrLn "All tests passed."

fibonacciSolutions :: [(String, Integer -> Integer)]
fibonacciSolutions =
  [ ("recursive", Problem2.sumEvenFib),
    ("tail", Problem2.sumEvenFibTail),
    ("modular", Problem2.sumEvenFibModular),
    ("map", Problem2.sumEvenFibMap),
    ("comprehension", Problem2.sumEvenFibComprehension),
    ("infinite", Problem2.sumEvenFibInfinite)
  ]

fibonacciCases :: [(Integer, Integer)]
fibonacciCases =
  [ (0, 0),
    (1, 0),
    (2, 2),
    (8, 10),
    (10, 10),
    (100, 44),
    (4000000, 4613732)
  ]

sundaySolutions :: [(String, Integer)]
sundaySolutions =
  [ ("recursive", Problem19.countSundaysRecursive),
    ("tail", Problem19.countSundaysTail),
    ("modular", Problem19.countSundaysModular),
    ("map", Problem19.countSundaysMap),
    ("comprehension", Problem19.countSundaysComprehension),
    ("infinite", Problem19.countSundaysInfinite)
  ]
