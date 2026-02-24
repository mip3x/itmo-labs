package ru.mip3x

import ru.mip3x.algo.BTree
import ru.mip3x.series.SinSeries
import kotlin.math.sin

fun main() {
    // val x = 0.5
    // val eps = 1e-10
    // val approx = SinSeries.sinTaylor(x, eps).value
    // val real = sin(x)
    //
    // println("approx = $approx")
    // println("real   = $real")
    // println("diff   = ${kotlin.math.abs(approx - real)}")

    val tree = BTree()

    val values = listOf(10, 80, 100, 10, 25, 10, 20, 55, 35, 70, 30, 40, 13, 12, 50, 26, 60, 22, 61)
    for (v in values) {
        tree.insert(v)
        println("insert $v -> ${tree.inOrder()}")
    }

    println("contains(30) = ${tree.contains(30)}")
    println("contains(999) = ${tree.contains(999)}")
}
