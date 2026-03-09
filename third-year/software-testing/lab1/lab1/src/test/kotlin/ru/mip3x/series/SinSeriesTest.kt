package ru.mip3x.series

import ru.mip3x.series.SinSeries

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows

import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.abs

class SinSeriesTest {
    // eps equivalent classes
    @Test
    fun errorOnZeroEps() {
        assertThrows<IllegalArgumentException> {
            SinSeries.sinTaylor(0.5, 0.0)
        }
    }

    @Test
    fun errorOnNegativeEps() {
        assertThrows<IllegalArgumentException> {
            SinSeries.sinTaylor(0.5, -1.0)
        }
    }

    // X equivalent classes
    @Test
    fun errorOnZeroX() {
        val r = SinSeries.sinTaylor(0.0, 1e-12).value
        assertEquals(0.0, r, 1e-15)
    }

    @Test
    fun sinPiOver4() {
        val r: Double = SinSeries.sinTaylor(PI / 4, 1e-12).value
        val expected: Double = sqrt(2.0) / 2
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun sinPiOver2() {
        val r: Double = SinSeries.sinTaylor(PI / 2, 1e-12).value
        val expected: Double = 1.0
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun sin3PiOver4() {
        val r: Double = SinSeries.sinTaylor((3 * PI) / 4, 1e-12).value
        val expected: Double = sqrt(2.0) / 2
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun sinPi() {
        val r: Double = SinSeries.sinTaylor(PI, 1e-12).value
        val expected: Double = 0.0
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun sin5PiOver4() {
        val r: Double = SinSeries.sinTaylor((5 * PI) / 4, 1e-12).value
        val expected: Double = -(sqrt(2.0) / 2)
        assertEquals(expected, r, 1e-12)
    }
    
    @Test
    fun sin3PiOver2() {
        val r: Double = SinSeries.sinTaylor((3 * PI) / 2, 1e-12).value
        val expected: Double = -1.0
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun sin7PiOver4() {
        val r: Double = SinSeries.sinTaylor((7 * PI) / 4, 1e-12).value
        val expected: Double = -(sqrt(2.0) / 2)
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun sin2PI() {
        val r: Double = SinSeries.sinTaylor(2 * PI, 1e-12).value
        val expected: Double = 0.0
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun sin15PiOver4() {
        val r: Double = SinSeries.sinTaylor((15 * PI) / 4, 1e-12).value
        val expected: Double = -(sqrt(2.0) / 2)
        assertEquals(expected, r, 1e-12)
    }

    @Test
    fun termsGetterIsCovered() {
        val res = SinSeries.sinTaylor(PI / 4, 1e-12)
        assertTrue(res.terms > 0)
    }

    @Test
    fun reducesAngleWhenLessThanMinusPi() {
        val r: Double = SinSeries.sinTaylor(-3 * PI / 2, 1e-12).value
        val expected: Double = 1.0
        assertEquals(expected, r, 1e-12)
    }

    // factorial
    @Test
    fun factorialThrowsOnNegative() {
        assertThrows<IllegalArgumentException> {
            SinSeries.factorial(-1)
        }
    }

    @Test
    fun factorialNoLoopFor0And1() {
        assertEquals(1.0, SinSeries.factorial(0), 0.0)
        assertEquals(1.0, SinSeries.factorial(1), 0.0)
    }

    @Test
    fun factorialLoopExecutesFor2Plus() {
        assertEquals(2.0, SinSeries.factorial(2), 0.0)
        assertEquals(6.0, SinSeries.factorial(3), 0.0)
    }

    // pow
    @Test
    fun powThrowsOnNegative() {
        assertThrows<IllegalArgumentException> {
            SinSeries.pow(2.0, -1)
        }
    }

    @Test
    fun powRepeatZeroTimesWhenNIs0() {
        assertEquals(1.0, SinSeries.pow(123.0, 0), 0.0)
    }

    @Test
    fun powRepeatExecutesWhenNPositive() {
        assertEquals(8.0, SinSeries.pow(2.0, 3), 1e-15)
        assertEquals(0.25, SinSeries.pow(0.5, 2), 1e-15)
    }

    // abs
    @Test
    fun absPositiveBranch() {
        assertEquals(2.5, SinSeries.abs(2.5), 0.0)
    }

    @Test
    fun absNonPositiveBranch() {
        assertEquals(2.5, SinSeries.abs(-2.5), 0.0)
        assertEquals(-0.0, SinSeries.abs(0.0), 0.0)
    }
}