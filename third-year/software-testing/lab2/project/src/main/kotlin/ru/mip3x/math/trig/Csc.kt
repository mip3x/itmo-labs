package ru.mip3x.math.trig

import ru.mip3x.math.Function

class Csc(
    private val sin: Function
) : Function {
    override fun calculate(x: Double, eps: Double) : Double {
        val sinValue = sin.calculate(x, eps)

        return 1.0 / sinValue
    }
}