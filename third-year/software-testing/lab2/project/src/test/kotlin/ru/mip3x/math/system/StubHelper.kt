package ru.mip3x.math.system

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.mip3x.math.Function

object StubHelper {
    fun functionStub(values: Map<Double, Double>): Function {
        val stub = mock<Function>()

        whenever(stub.calculate(any(), any())).thenAnswer { invocation ->
            val x = invocation.getArgument<Double>(0)

            values[x] ?: throw IllegalArgumentException(
                "No stub value for x=$x"
            )
        }

        return stub
    }
}