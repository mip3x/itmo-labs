package ru.mip3x.math.trig

import ru.mip3x.math.Function

class Cot(
    private val sin: Function,
    private val cos: Function
) : Function {
    override fun calculate(x: Double, eps: Double) : Double {
        val sinValue = sin.calculate(x, eps)
        val cosValue = cos.calculate(x, eps) 

        return cosValue / sinValue
    }
}