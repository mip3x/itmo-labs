package ru.mip3x.series.sin

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

    fun sinTaylor(x: Double, eps: Double) : Double {
        require(eps > 0)

        var sum = x

        var degree = 3
        var next: Double
        var func : (Double, Double) -> Double = ::substract

        do {
            next = pow(x, degree) / factorial(degree)
            sum = apply(sum, next, func)
            
            func = if (func == ::substract) ::summarize else ::substract
            degree += 2
        } while (abs(next) > eps)

        return sum;
    }
}