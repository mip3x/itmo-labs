package ru.mip3x.math.base;

import ru.mip3x.math.Function
import kotlin.math.abs

abstract class SeriesExpander(
    private val maxIterations: Int = 10_000
) : Function {
    override fun calculate(x: Double, eps: Double) : Double {
        require(x.isFinite()) { "x must be finite" }
        require(eps.isFinite() && eps > 0.0) { "eps must be > 0 and finite" }
        require(isDefinedAt(x)) 

        val preparedX = prepareX(x)

        var sum = 0.0
        var n = 0

        while (n < maxIterations) {
            val term = calculateTerm(preparedX, n)
            sum += term

            if (abs(term) <= eps) {
                return sum
            }

            n++
        }

        throw IllegalStateException(
            "Series did not converge for x=$x with eps=$eps in $maxIterations iterations"
        )
    }

    protected open fun prepareX(x: Double): Double {
        return x
    }

    protected abstract fun isDefinedAt(x: Double): Boolean

    protected abstract fun calculateTerm(x: Double, n: Int): Double
}