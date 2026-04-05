package ru.mip3x.math.system

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import ru.mip3x.math.base.Sin

class SystemFunctionSinIntegrationTest {
    private val trigResource = "/test-data/system/trig_values.csv"
    private val logResource = "/test-data/system/log_values.csv"

    private val sin = Sin()
    private val cos = StubHelper.functionStub(trigResource, "cos")
    private val tan = StubHelper.functionStub(trigResource, "tan")
    private val cot = StubHelper.functionStub(trigResource, "cot")
    private val sec = StubHelper.functionStub(trigResource, "sec")
    private val csc = StubHelper.functionStub(trigResource, "csc")
    private val ln = StubHelper.functionStub(logResource, "ln")
    private val log2 = StubHelper.functionStub(logResource, "log2")
    private val log3 = StubHelper.functionStub(logResource, "log3")
    private val log10 = StubHelper.functionStub(logResource, "log10")

    private val system = SystemFunction(
        sin, cos, tan, cot, sec, csc,
        ln, log2, log3, log10
    )

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/system/trig_values.csv"],
        numLinesToSkip = 1
    )
    fun trigBranchRealSinTest(
        x: Double,
        expectedSin: Double,
        cosX: Double,
        tanX: Double,
        cotX: Double,
        secX: Double,
        cscX: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = system.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }
}
