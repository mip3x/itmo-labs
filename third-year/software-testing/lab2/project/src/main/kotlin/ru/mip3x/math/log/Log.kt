package ru.mip3x.math.log

import ru.mip3x.math.Function

class Log(
    private val ln: Function,
    private val base: Double
) : Function {
    override fun calculate(x: Double, eps: Double) : Double {
        require(base.isFinite() && base > 0.0 && base != 1.0) {
            "Logarithm base must be positive, finite and not equal to 1"
        }

        // log_b(x) / log_b(a) = log_a(x)
        // => ln(x) / ln(a) = log_a(x)
        return ln.calculate(x, eps) / ln.calculate(base, eps)
    }
}