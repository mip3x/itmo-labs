package ru.mip3x.math

interface Function {
    fun calculate(x: Double, eps: Double) : Double
}