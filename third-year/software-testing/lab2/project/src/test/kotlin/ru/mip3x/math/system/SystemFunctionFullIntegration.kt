package ru.mip3x.math.system

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.mip3x.math.base.Sin
import ru.mip3x.math.base.Ln
import ru.mip3x.math.trig.Cos
import ru.mip3x.math.trig.Csc
import ru.mip3x.math.trig.Sec
import ru.mip3x.math.trig.Tan
import ru.mip3x.math.trig.Cot
import ru.mip3x.math.log.Log

class SystemFunctionFullIntegrationTest {
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

    @ParameterizedTest(name = "real sin, cos, csc, sec; stubs for others, x={0}")
    @CsvSource(
        "-1.0",
        "-2.0"
    )
    fun trigBranchRealSinCosCscSecTest(
        x: Double,
    ) {
        val actual = system.calculate(x, eps)

        val sinX = sin.calculate(x, eps)
        val cosX = cos.calculate(x, eps)
        val cscX = csc.calculate(x, eps)
        val secX = sec.calculate(x, eps)
        val tanX = tan.calculate(x, eps)
        val cotX = cot.calculate(x, eps)

        val tan3 = tanX * tanX * tanX
        val sec3 = secX * secX * secX
        val inner = cscX * (tanX / cotX)
        val inner2 = inner * inner

        val leftPart =
            ((((((((cosX - secX) * cotX) - cotX) * cotX) - tanX) / cosX) - cscX) -
                (((cosX - sinX) + (secX - cotX)) / cscX)) -
                ((tan3 + tanX) / tanX)

        val rightPart =
            (secX - (tanX / tan3) * sec3) * inner2

        val expected = leftPart * rightPart

        assertEquals(expected, actual, 1e-9)
    }
}