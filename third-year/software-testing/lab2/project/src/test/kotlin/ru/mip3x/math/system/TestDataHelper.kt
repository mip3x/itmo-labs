package ru.mip3x.math.system

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
}
