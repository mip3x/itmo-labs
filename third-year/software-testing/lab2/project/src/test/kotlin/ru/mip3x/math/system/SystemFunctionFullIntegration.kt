package ru.mip3x.math.system

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import ru.mip3x.math.base.Sin
import ru.mip3x.math.base.Ln
import ru.mip3x.math.trig.Cos
import ru.mip3x.math.trig.Csc
import ru.mip3x.math.trig.Sec
import ru.mip3x.math.trig.Tan
import ru.mip3x.math.trig.Cot
import ru.mip3x.math.log.Log

class SystemFunctionFullIntegrationTest {
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

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/system/trig_values.csv"],
        numLinesToSkip = 1
    )
    fun trigBranchRealSinCosCscSecTest(
        x: Double,
        sinX: Double,
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
