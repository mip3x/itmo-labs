package ru.mip3x

import ru.mip3x.series.SinSeries
import kotlin.math.sin

fun main() {
    val x = 0.5
    val eps = 1e-10
    val approx = SinSeries.sinTaylor(x, eps).value
    val real = sin(x)

    println("approx = $approx")
    println("real   = $real")
    println("diff   = ${kotlin.math.abs(approx - real)}")
}