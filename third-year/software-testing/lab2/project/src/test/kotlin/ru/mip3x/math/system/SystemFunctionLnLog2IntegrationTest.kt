package ru.mip3x.math.system

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.mip3x.math.base.Ln
import ru.mip3x.math.log.Log

class SystemFunctionLnLog2IntegrationTest {
    private val eps = 1e-6

    private val sin = StubHelper.functionStub(StubValues.sinValues)
    private val cos = StubHelper.functionStub(StubValues.cosValues)
    private val tan = StubHelper.functionStub(StubValues.tanValues)
    private val cot = StubHelper.functionStub(StubValues.cotValues)
    private val sec = StubHelper.functionStub(StubValues.secValues)
    private val csc = StubHelper.functionStub(StubValues.cscValues)
    private val ln = Ln()
    private val log2 = Log(ln, 2.0)
    private val log3 = StubHelper.functionStub(StubValues.log3Values)
    private val log10 = StubHelper.functionStub(StubValues.log10Values)

    private val system = SystemFunction(
        sin, cos, tan, cot, sec, csc,
        ln, log2, log3, log10
    )

    @ParameterizedTest(name = "real ln, log2; stubs for others, x={0}")
    @CsvSource(
        "1.0, 4.0, 5.0",
        "2.0, 5.0, 6.0"
    )
    fun logBranchRealLnLog2Test(
        x: Double,
        log3X: Double,
        log10X: Double
    ) {
        val actual = system.calculate(x, eps)

        val lnX = ln.calculate(x, eps)
        val log2X = log2.calculate(x, eps)

        val product = log10X * log3X
        val product3 = product * product * product
        val sum = product3 + log2X
        val sum3 = sum * sum * sum

        val expected = sum3 * lnX

        assertEquals(expected, actual, 1e-9)
    }
}
