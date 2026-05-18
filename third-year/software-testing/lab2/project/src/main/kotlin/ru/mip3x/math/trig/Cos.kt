package ru.mip3x.math.trig

import ru.mip3x.math.Function
import kotlin.math.PI

class Cos(
    private val sin: Function
) : Function {
    override fun calculate(x: Double, eps: Double) : Double {
        return sin.calculate(x + PI / 2.0, eps)
    }
}