package ru.mip3x.export

import ru.mip3x.math.Function
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs

class CsvExporter(
    private val delimiter: Char = ','
) {
    fun export(
        function: Function,
        outputPath: Path,
        startX: Double,
        endX: Double,
        step: Double,
        eps: Double
    ) {
        require(startX.isFinite() && endX.isFinite()) {
            "Range bounds must be finite"
        }
        require(step.isFinite() && step > 0.0) {
            "Step must be finite and positive"
        }
        require(eps.isFinite() && eps > 0.0) {
            "Epsilon must be finite and positive"
        }
        require(startX <= endX) {
            "startX must be less than or equal to endX"
        }

        outputPath.parent?.let {
            Files.createDirectories(it)
        }

        Files.newBufferedWriter(outputPath).use { writer ->
            writer.append("X")
            writer.append(delimiter)
            writer.append("Result")
            writer.newLine()

            var x = startX
            while (x <= endX + stepTolerance(step)) {
                val value = try {
                    function.calculate(x, eps)
                } catch (_: IllegalArgumentException) {
                    Double.NaN
                } catch (_: IllegalStateException) {
                    Double.NaN
                }

                writer.append(x.toString())
                writer.append(delimiter)
                writer.append(value.toString())
                writer.newLine()

                x += step
            }
        }
    }

    private fun stepTolerance(step: Double): Double {
        return abs(step) * 1e-9
    }
}
