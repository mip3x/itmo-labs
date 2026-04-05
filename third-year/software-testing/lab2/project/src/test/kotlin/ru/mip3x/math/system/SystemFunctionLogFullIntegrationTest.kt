package ru.mip3x.math.system

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.mip3x.math.base.Ln
import ru.mip3x.math.base.Sin
import ru.mip3x.math.log.Log
import ru.mip3x.math.trig.Cos
import ru.mip3x.math.trig.Cot
import ru.mip3x.math.trig.Csc
import ru.mip3x.math.trig.Sec
import ru.mip3x.math.trig.Tan

class SystemFunctionLogFullIntegrationTest {
    private val eps = 1e-6

    private val sin = Sin()
    private val cos = Cos(sin)
    private val tan = Tan(sin, cos)
    private val cot = Cot(sin, cos)
    private val sec = Sec(cos)
    private val csc = Csc(sin)
    private val ln = Ln()
    private val log2 = Log(ln, 2.0)
    private val log3 = Log(ln, 3.0)
    private val log10 = Log(ln, 10.0)

    private val system = SystemFunction(
        sin, cos, tan, cot, sec, csc,
        ln, log2, log3, log10
    )

    @ParameterizedTest(name = "real ln, log2, log3, log10; x={0}")
    @CsvSource(
        "1.0",
        "2.0"
    )
    fun logBranchFullIntegrationTest(
        x: Double
    ) {
        val actual = system.calculate(x, eps)

        val lnX = ln.calculate(x, eps)
        val log2X = log2.calculate(x, eps)
        val log3X = log3.calculate(x, eps)
        val log10X = log10.calculate(x, eps)

        val product = log10X * log3X
        val product3 = product * product * product
        val sum = product3 + log2X
        val sum3 = sum * sum * sum

        val expected = sum3 * lnX

        assertEquals(expected, actual, 1e-9)
    }
}
