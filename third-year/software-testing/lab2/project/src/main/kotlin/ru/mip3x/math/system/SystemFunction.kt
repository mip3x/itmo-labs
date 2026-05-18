package ru.mip3x.math.system

import ru.mip3x.math.Function

class SystemFunction(
    private val sin: Function,
    private val cos: Function,
    private val tan: Function,
    private val cot: Function,
    private val sec: Function,
    private val csc: Function,
    private val ln: Function,
    private val log2: Function,
    private val log3: Function,
    private val log10: Function
) : Function {
    override fun calculate(x: Double, eps: Double) : Double {
        return if (x <= 0.0) {
            calculateTrigonometricBranch(x, eps)
        } else {
            calculateLogarithmicBranch(x, eps)
        }
    }

    private fun calculateTrigonometricBranch(x: Double, eps: Double): Double {
        val sinX = sin.calculate(x, eps)
        val cosX = cos.calculate(x, eps)
        val tanX = tan.calculate(x, eps)
        val cotX = cot.calculate(x, eps)
        val secX = sec.calculate(x, eps)
        val cscX = csc.calculate(x, eps)

        val leftPart =
            (((((((((cosX - secX) * cotX) - cotX) * cotX) - tanX) / cosX) - cscX) -
                ((cosX - sinX) + (secX - cotX))) / cscX) -
                ((pow(tanX, 3) + tanX) / tanX)

        val rightPart =
            (secX - (tanX / pow(tanX, 3)) * pow(secX, 3)) *
                pow(cscX * (tanX / cotX), 2)

        return leftPart * rightPart
    }

    private fun calculateLogarithmicBranch(x: Double, eps: Double): Double {
        val lnX = ln.calculate(x, eps)
        val log2X = log2.calculate(x, eps)
        val log3X = log3.calculate(x, eps)
        val log10X = log10.calculate(x, eps)

        return pow(pow(log10X * log3X, 3) + log2X, 3) * lnX
    }

    private fun pow(value: Double, power: Int): Double {
        var result = 1.0
        repeat(power) {
            result *= value
        }
        return result
    }
}
