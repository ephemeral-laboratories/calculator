package garden.ephemeral.calculator.nodes.ops

import garden.ephemeral.math.complex.Complex

enum class PrefixOperator(
    val printedSymbol: String,
    val realFunction: (Double) -> Double,
    val complexFunction: (Complex) -> Complex,
) {
    UNARY_MINUS("-", Double::unaryMinus, Complex::unaryMinus),
    ;

    fun apply(value: Any): Any {
        return when (value) {
            is Double -> realFunction(value)
            is Complex -> complexFunction(value)
            else -> throw IllegalStateException("Cannot apply function $name to value $value")
        }
    }
}