package ru.mip3x.math.trig

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import ru.mip3x.math.base.Sin

class TrigTest {
    private val sin = Sin()
    private val cos = Cos(sin)
    private val tan = Tan(sin, cos)
    private val cot = Cot(sin, cos)
    private val sec = Sec(cos)
    private val csc = Csc(sin)

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/trigonometry/cos_values.csv"],
        numLinesToSkip = 1
    )
    fun cosTest(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = cos.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/trigonometry/tan_values.csv"],
        numLinesToSkip = 1
    )
    fun tanTest(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = tan.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/trigonometry/cot_values.csv"],
        numLinesToSkip = 1
    )
    fun cotTest(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = cot.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/trigonometry/sec_values.csv"],
        numLinesToSkip = 1
    )
    fun secTest(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = sec.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/trigonometry/csc_values.csv"],
        numLinesToSkip = 1
    )
    fun cscTest(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = csc.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }
}