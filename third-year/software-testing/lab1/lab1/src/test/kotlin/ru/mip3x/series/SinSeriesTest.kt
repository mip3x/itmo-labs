package ru.mip3x.series

import ru.mip3x.series.SinSeries

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows

import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.abs

class SinSeriesTest {
    private fun assertCloseToStd(
        x: Double,
        eps: Double,
        tolerance: Double) {
            val approx: Double = SinSeries.sinTaylor(x, eps).value
            val expected: Double = sin(x)

            assertTrue(abs(approx - expected) <= tolerance)
        }

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
    fun verySmallX() {
        assertCloseToStd(1e-6, 1e-12, 1e-15)
    }

    @Test
    fun mediumX() {
        assertCloseToStd(0.5, 1e-12, 1e-12)
    }

    @Test
    fun xNearPIOver2() {
        assertCloseToStd(PI / 2, 1e-12, 1e-10)
    }

    @Test
    fun xNearPI() {
        assertCloseToStd(PI, 1e-12, 1e-10)
    }

    @Test
    fun xNear3PIOver2() {
        assertCloseToStd((3 * PI) / 2, 1e-12, 1e-10)
    }

    @Test 
    fun largeX() {
        assertCloseToStd(20.0, 1e-12, 1e-8)
    }
}