package ru.mip3x.export

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.mip3x.math.Function
import java.nio.file.Files
import java.nio.file.Path

class CsvExporterTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun exportWritesValuesForEachPointInRange() {
        val exporter = CsvExporter(';')
        val function = object : Function {
            override fun calculate(x: Double, eps: Double): Double {
                return x * x
            }
        }
        val output = tempDir.resolve("square.csv")

        exporter.export(
            function = function,
            outputPath = output,
            startX = 0.0,
            endX = 1.0,
            step = 0.5,
            eps = 1e-6
        )

        val lines = Files.readAllLines(output)

        assertEquals(
            listOf(
                "X;Result",
                "0.0;0.0",
                "0.5;0.25",
                "1.0;1.0"
            ),
            lines
        )
    }

    @Test
    fun exportWritesNaNForUndefinedPoints() {
        val exporter = CsvExporter()
        val function = object : Function {
            override fun calculate(x: Double, eps: Double): Double {
                require(x >= 0.0) { "x must be non-negative" }
                return x
            }
        }
        val output = tempDir.resolve("domain.csv")

        exporter.export(
            function = function,
            outputPath = output,
            startX = -1.0,
            endX = 1.0,
            step = 1.0,
            eps = 1e-6
        )

        val lines = Files.readAllLines(output)

        assertEquals("X,Result", lines[0])
        assertTrue(lines.contains("-1.0,NaN"))
        assertTrue(lines.contains("0.0,0.0"))
        assertTrue(lines.contains("1.0,1.0"))
    }
}
