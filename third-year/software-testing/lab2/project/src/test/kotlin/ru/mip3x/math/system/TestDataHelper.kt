package ru.mip3x.math.system

import kotlin.math.PI

object TestDataHelper {
    fun loadColumnAsMap(resourcePath: String, valueColumn: String): Map<Double, Double> {
        val lines = this::class.java.getResourceAsStream(resourcePath)
            ?.bufferedReader()
            ?.readLines()
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")

        require(lines.isNotEmpty()) { "Resource is empty: $resourcePath" }

        val headers = lines.first().split(",")
        val xIndex = headers.indexOf("x")
        val valueIndex = headers.indexOf(valueColumn)

        require(xIndex >= 0) { "Column 'x' not found in $resourcePath" }
        require(valueIndex >= 0) { "Column '$valueColumn' not found in $resourcePath" }

        return lines
            .drop(1)
            .filter { it.isNotBlank() }
            .associate { line ->
                val values = line.split(",")
                values[xIndex].toDouble() to values[valueIndex].toDouble()
            }
    }

    fun loadSinAndShiftedCosAsMap(resourcePath: String): Map<Double, Double> {
        val lines = this::class.java.getResourceAsStream(resourcePath)
            ?.bufferedReader()
            ?.readLines()
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")

        require(lines.isNotEmpty()) { "Resource is empty: $resourcePath" }

        val headers = lines.first().split(",")
        val xIndex = headers.indexOf("x")
        val sinIndex = headers.indexOf("sin")
        val cosIndex = headers.indexOf("cos")

        require(xIndex >= 0) { "Column 'x' not found in $resourcePath" }
        require(sinIndex >= 0) { "Column 'sin' not found in $resourcePath" }
        require(cosIndex >= 0) { "Column 'cos' not found in $resourcePath" }

        val values = mutableMapOf<Double, Double>()

        lines
            .drop(1)
            .filter { it.isNotBlank() }
            .forEach { line ->
                val row = line.split(",")
                val x = row[xIndex].toDouble()
                val sin = row[sinIndex].toDouble()
                val cos = row[cosIndex].toDouble()

                values[x] = sin
                values[x + PI / 2.0] = cos
            }

        return values
    }
}
