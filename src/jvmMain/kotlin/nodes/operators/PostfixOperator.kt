package garden.ephemeral.calculator.nodes.operators

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real

private val RADIANS_PER_DEGREE = Real.TAU / Real.valueOf(360)

enum class PostfixOperator(
    val printedSymbol: String,
    val realFunction: (Real) -> Real,
    val complexFunction: (Complex) -> Complex,
) {
    DEGREES("°", { x -> x * RADIANS_PER_DEGREE }, { z -> z * RADIANS_PER_DEGREE }),
    ;

    fun apply(value: Any): Any {
        return when (value) {
            is Real -> realFunction(value)
            is Complex -> complexFunction(value)
            else -> throw IllegalStateException("Cannot apply function $name to value $value")
        }
    }
}
