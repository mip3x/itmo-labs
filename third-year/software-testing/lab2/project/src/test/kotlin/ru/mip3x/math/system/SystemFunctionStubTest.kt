package ru.mip3x.math.system

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SystemFunctionStubParameterizedTest {
    private val eps = 1e-6

    private val sin = StubHelper.functionStub(StubValues.sinValues)
    private val cos = StubHelper.functionStub(StubValues.cosValues)
    private val tan = StubHelper.functionStub(StubValues.tanValues)
    private val cot = StubHelper.functionStub(StubValues.cotValues)
    private val sec = StubHelper.functionStub(StubValues.secValues)
    private val csc = StubHelper.functionStub(StubValues.cscValues)
    private val ln = StubHelper.functionStub(StubValues.lnValues)
    private val log2 = StubHelper.functionStub(StubValues.log2Values)
    private val log3 = StubHelper.functionStub(StubValues.log3Values)
    private val log10 = StubHelper.functionStub(StubValues.log10Values)

    private val system = SystemFunction(
        sin, cos, tan, cot, sec, csc,
        ln, log2, log3, log10
    )

    @ParameterizedTest(name = "trig branch x={0}")
    @CsvSource(
        "-1.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0",
        "-2.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0"
    )
    fun trigBranchStubTest(
        x: Double,
        sinX: Double,
        cosX: Double,
        tanX: Double,
        cotX: Double,
        secX: Double,
        cscX: Double
    ) {
        val actual = system.calculate(x, eps)

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

    @ParameterizedTest(name = "log branch x={0}")
    @CsvSource(
        "1.0, 2.0, 3.0, 4.0, 5.0",
        "2.0, 3.0, 4.0, 5.0, 6.0"
    )
    fun logBranchStubTest(
        x: Double,
        lnX: Double,
        log2X: Double,
        log3X: Double,
        log10X: Double
    ) {
        val actual = system.calculate(x, eps)

        val product = log10X * log3X
        val product3 = product * product * product
        val sum = product3 + log2X
        val sum3 = sum * sum * sum

        val expected = sum3 * lnX

        assertEquals(expected, actual, 1e-9)
    }
}