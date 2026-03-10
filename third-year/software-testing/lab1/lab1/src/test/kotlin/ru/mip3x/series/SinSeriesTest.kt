package ru.mip3x.series

import ru.mip3x.series.SinSeries

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.ArgumentsAggregator
import org.junit.jupiter.params.aggregator.AggregateWith
import org.junit.jupiter.api.extension.ParameterContext

import java.util.stream.Stream

data class SinCase(
    val x: Double,
    val expected: Double
)

class SinCaseAggregator : ArgumentsAggregator {
    override fun aggregateArguments(accessor: ArgumentsAccessor, context: ParameterContext): Any {
        return SinCase(
            accessor.getDouble(0),
            accessor.getDouble(1)
        )
    }
}

class SinSeriesTest {
    // eps equivalent classes
    @ParameterizedTest(name = "throw err for eps in {0}")
    @ValueSource(doubles = [0.0, -1.0])
    fun errorOnInvalidEps(eps: Double) {
        assertThrows<IllegalArgumentException> {
            SinSeries.sinTaylor(0.5, eps)
        }
    }

    // X equivalent classes
    @ParameterizedTest(name = "sinTaylor({0}) = {1}")
    @MethodSource("sinTaylorCases")
    fun sinTaylorReturnsExpectedValue(
        @AggregateWith(SinCaseAggregator::class) case: SinCase
    ) {
        val actual = SinSeries.sinTaylor(case.x, 1e-12).value
        assertEquals(case.expected, actual, 1e-12)
    }

    companion object {
        @JvmStatic
        fun sinTaylorCases(): Stream<Arguments> = Stream.of(
            Arguments.of(0.0, 0.0),
            Arguments.of(3.141592653589793 / 4, 1.4142135623730951 / 2),
            Arguments.of(3.141592653589793 / 2, 1.0),
            Arguments.of((3 * 3.141592653589793) / 4, 1.4142135623730951 / 2),
            Arguments.of(3.141592653589793, 0.0),
            Arguments.of((5 * 3.141592653589793) / 4, -(1.4142135623730951 / 2)),
            Arguments.of((3 * 3.141592653589793) / 2, -1.0),
            Arguments.of((7 * 3.141592653589793) / 4, -(1.4142135623730951 / 2)),
            Arguments.of(2 * 3.141592653589793, 0.0),
            Arguments.of((15 * 3.141592653589793) / 4, -(1.4142135623730951 / 2)),
            Arguments.of(-3 * 3.141592653589793 / 2, 1.0)
        )
    }

    // termsGetter
    @Test
    fun termsGetterIsCovered() {
        val res = SinSeries.sinTaylor(3.141592653589793 / 4, 1e-12)
        assertTrue(res.terms > 0)
    }

    // factorial
    @ParameterizedTest(name = "factorial should throw for n = {0}")
    @ValueSource(ints = [-1])
    fun factorialThrowsOnNegative(n: Int) {
        assertThrows<IllegalArgumentException> {
            SinSeries.factorial(n)
        }
    }

    @ParameterizedTest(name = "factorial({0}) = {1}")
    @CsvSource(
        "0, 1.0",
        "1, 1.0",
        "2, 2.0",
        "3, 6.0"
    )
    fun factorialReturnsExpectedValue(args: ArgumentsAccessor) {
        assertEquals(args.getDouble(1), SinSeries.factorial(args.getInteger(0)), 0.0)
    }

    // pow
    @ParameterizedTest(name = "pow should throw for n = {0}")
    @ValueSource(ints = [-1])
    fun powThrowsOnNegative(n: Int) {
        assertThrows<IllegalArgumentException> {
            SinSeries.pow(2.0, n)
        }
    }

    @ParameterizedTest(name = "pow({0}, {1}) = {2}")
    @CsvSource(
        "123.0, 0, 1.0",
        "2.0, 3, 8.0",
        "0.5, 2, 0.25"
    )
    fun powReturnsExpectedValue(x: Double, n: Int, expected: Double) {
        assertEquals(expected, SinSeries.pow(x, n), 1e-15)
    }

    // abs
    @ParameterizedTest(name = "abs({0}) = {1}")
    @CsvSource(
        "2.5, 2.5",
        "-2.5, 2.5",
        "0.0, 0.0"
    )
    fun absReturnsExpectedValue(x: Double, expected: Double) {
        assertEquals(expected, SinSeries.abs(x), 0.0)
    }
}