package ru.mip3x.math.base

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class SeriesTest {
    private val sin = Sin()
    private val ln = Ln()

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/trigonometry/sin_values.csv"],
        numLinesToSkip = 1
    )
    fun sinSeriesTest(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = sin.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/trigonometry/sin_invalid_values.csv"],
        numLinesToSkip = 1
    )
    fun sinSeriesInvalidArgumentsTest(x: Double, eps: Double) {
        assertThrows(IllegalArgumentException::class.java) {
            sin.calculate(x, eps)
        }
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/logarithmic/ln_values.csv"],
        numLinesToSkip = 1
    )
    fun lnSeriesTest(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val actual = ln.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/logarithmic/ln_invalid_values.csv"],
        numLinesToSkip = 1
    )
    fun lnSeriesInvalidArgumentsTest(x: Double, eps: Double) {
        assertThrows(IllegalArgumentException::class.java) {
            ln.calculate(x, eps)
        }
    }
}