package ru.mip3x.math.trig

import ru.mip3x.math.Function

class Sec(
    private val cos: Function
) : Function {
    override fun calculate(x: Double, eps: Double) : Double {
        val cosValue = cos.calculate(x, eps)

        return 1.0 / cosValue
    }
}