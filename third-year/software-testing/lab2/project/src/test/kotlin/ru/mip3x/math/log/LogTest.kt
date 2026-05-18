package ru.mip3x.math.log

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import ru.mip3x.math.base.Ln

class LogTest {
    private val ln = Ln()

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/logarithmic/log2_values.csv"],
        numLinesToSkip = 1
    )
    fun log2Test(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val log2 = Log(ln, 2.0)
        val actual = log2.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/logarithmic/log3_values.csv"],
        numLinesToSkip = 1
    )
    fun log3Test(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val log3 = Log(ln, 3.0)
        val actual = log3.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/logarithmic/log10_values.csv"],
        numLinesToSkip = 1
    )
    fun log10Test(
        x: Double,
        expected: Double,
        resultEpsilon: Double,
        convergenceEpsilon: Double
    ) {
        val log10 = Log(ln, 10.0)
        val actual = log10.calculate(x, convergenceEpsilon)
        assertEquals(expected, actual, resultEpsilon)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/logarithmic/log_invalid_values.csv"],
        numLinesToSkip = 1
    )
    fun logInvalidXTest(x: Double, eps: Double) {
        val log2 = Log(ln, 2.0)

        assertThrows(IllegalArgumentException::class.java) {
            log2.calculate(x, eps)
        }
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/test-data/logarithmic/log_invalid_bases.csv"],
        numLinesToSkip = 1
    )
    fun logInvalidBaseTest(base: Double, x: Double, eps: Double) {
        val log = Log(ln, base)

        assertThrows(IllegalArgumentException::class.java) {
            log.calculate(x, eps)
        }
    }
}