package ru.mip3x.app

import ru.mip3x.math.Function
import ru.mip3x.math.base.Ln
import ru.mip3x.math.base.Sin
import ru.mip3x.math.log.Log
import ru.mip3x.math.system.SystemFunction
import ru.mip3x.math.trig.Cos
import ru.mip3x.math.trig.Cot
import ru.mip3x.math.trig.Csc
import ru.mip3x.math.trig.Sec
import ru.mip3x.math.trig.Tan

class FunctionRegistry {
    private val sin = Sin()
    private val cos = Cos(sin)
    private val tan = Tan(sin, cos)
    private val cot = Cot(sin, cos)
    private val sec = Sec(cos)
    private val csc = Csc(sin)
    private val ln = Ln()
    private val log2 = Log(ln, 2.0)
    private val log3 = Log(ln, 3.0)
    private val log10 = Log(ln, 10.0)
    private val system = SystemFunction(
        sin, cos, tan, cot, sec, csc,
        ln, log2, log3, log10
    )

    private val functions = mapOf(
        "sin" to sin,
        "cos" to cos,
        "tan" to tan,
        "cot" to cot,
        "sec" to sec,
        "csc" to csc,
        "ln" to ln,
        "log2" to log2,
        "log3" to log3,
        "log10" to log10,
        "system" to system
    )

    fun get(name: String): Function {
        val function = functions[name.lowercase()]
        require(function != null) {
            "Unknown module '$name'. Available modules: ${functions.keys.sorted().joinToString(", ")}"
        }

        return function
    }
}
