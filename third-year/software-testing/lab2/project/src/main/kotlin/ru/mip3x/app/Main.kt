package ru.mip3x.app

import ru.mip3x.export.CsvExporter
import java.nio.file.Path

fun main(args: Array<String>) {
    if (args.size !in 6..7) {
        printUsage()
        return
    }

    val moduleName = args[0]
    val outputPath = Path.of(args[1])
    val startX = args[2].toDoubleOrNull()
    val endX = args[3].toDoubleOrNull()
    val step = args[4].toDoubleOrNull()
    val eps = args[5].toDoubleOrNull()
    val delimiter = args.getOrNull(6)?.singleOrNull() ?: ','

    require(startX != null && endX != null && step != null && eps != null) {
        "startX, endX, step and eps must be valid numbers"
    }

    val registry = FunctionRegistry()
    val exporter = CsvExporter(delimiter)

    exporter.export(
        function = registry.get(moduleName),
        outputPath = outputPath,
        startX = startX,
        endX = endX,
        step = step,
        eps = eps
    )

    println("CSV exported to ${outputPath.toAbsolutePath()}")
}

private fun printUsage() {
    println(
        """
        Usage:
          ./gradlew run --args="<module> <output-path> <startX> <endX> <step> <eps> [delimiter]"

        Available modules:
          sin, cos, tan, cot, sec, csc, ln, log2, log3, log10, system

        Example:
          ./gradlew run --args="system build/system.csv -10 10 0.1 1e-6 ;"
        """.trimIndent()
    )
}
