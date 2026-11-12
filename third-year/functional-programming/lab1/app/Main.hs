module Main where

import qualified Problem19
import qualified Problem2

main :: IO ()
main = do
  putStrLn "Problem 2:"
  print (Problem2.sumEvenFib 4000000)
  putStrLn "Problem 19:"
  print Problem19.countSundaysRecursive
