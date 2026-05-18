package ru.mip3x.math.base

class Ln : SeriesExpander() {
    override fun isDefinedAt(x: Double): Boolean = x.isFinite() && x > 0.0

    override fun calculateTerm(x: Double, n: Int): Double {
        val t = (x - 1.0) / (x + 1.0)
        val power = 2 * n + 1

        var numerator = 1.0
        repeat(power) {
            numerator *= t
        }

        return 2.0 * numerator / power.toDouble()
    }
}
