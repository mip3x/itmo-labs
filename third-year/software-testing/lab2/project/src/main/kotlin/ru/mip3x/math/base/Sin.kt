package ru.mip3x.math.base

import kotlin.math.PI

class Sin: SeriesExpander() {
    override fun isDefinedAt(x: Double) : Boolean {
        return x.isFinite()
    }

    override fun prepareX(x: Double) : Double {
        var normalized = x % (2 * PI)

        if (normalized > PI) {
            normalized -= 2 * PI
        } else if (normalized < -PI) {
            normalized += 2 * PI
        }

        return normalized
    }

    override fun calculateTerm(x: Double, n: Int) : Double {
        val sign = if (n % 2 == 0) 1.0 else -1.0
        val power = 2 * n + 1

        var numerator = 1.0
        repeat(power) {
            numerator *= x
        }

        var denominator = 1.0
        for (i in 2..power) {
            denominator *= i.toDouble()
        }

        return sign * numerator / denominator
    }
}