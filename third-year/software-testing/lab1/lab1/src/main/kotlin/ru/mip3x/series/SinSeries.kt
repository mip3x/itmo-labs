package ru.mip3x.series

data class SinSeriesResults(
    val value: Double,
    val lastTermAbs: Double,
    val terms: Int
)

object SinSeries {
    fun summarize(x: Double, y: Double) : Double {
        return x + y
    } 

    fun substract(x: Double, y: Double) : Double {
        return x - y
    }

    fun apply(x: Double, y: Double, f: (Double, Double) -> Double) : Double {
        return f(x, y)
    }

    fun factorial(n: Int): Double {
        require(n >= 0)

        var res = 1.0
        for (i in 2..n) {
            res *= i
        }
        return res
    }

    fun pow(x: Double, n: Int): Double {
        require(n >= 0)

        var res = 1.0
        repeat(n) {
            res *= x
        }
        return res
    }

    fun abs(x: Double): Double {
        if (x > 0) {
            return x
        }
        return -x
    }

    fun sinTaylor(x: Double, eps: Double) : SinSeriesResults {
        require(eps > 0)

        var sum = x
        var terms = 1

        var degree = 3
        var term: Double
        var func : (Double, Double) -> Double = ::substract

        do {
            term = pow(x, degree) / factorial(degree)
            terms++

            sum = apply(sum, term, func)
            
            func = if (func == ::substract) ::summarize else ::substract
            degree += 2
        } while (abs(term) > eps)

        return SinSeriesResults(sum, abs(term), terms)
    }
}