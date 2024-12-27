package garden.ephemeral.calculator.operators

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.values.Value

private val RADIANS_PER_DEGREE = Real.TAU / Real.valueOf(360)

enum class PostfixOperator(
    val printedSymbol: String,
    val realFunction: (Real) -> Real,
    val complexFunction: (Complex) -> Complex,
) {
    DEGREES("°", { it * RADIANS_PER_DEGREE }, { it * RADIANS_PER_DEGREE }),
    ;

    fun apply(value: Value): Value = when (value) {
        is Value.OfReal -> Value.OfReal(realFunction(value.value))
        is Value.OfComplex -> Value.OfComplex(complexFunction(value.value))
    }
}