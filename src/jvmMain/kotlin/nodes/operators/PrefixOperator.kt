package garden.ephemeral.calculator.nodes.operators

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real

enum class PrefixOperator(
    val printedSymbol: String,
    val realFunction: (Real) -> Real,
    val complexFunction: (Complex) -> Complex,
) {
    UNARY_MINUS("-", Real::unaryMinus, Complex::unaryMinus),
    ;

    fun apply(value: Any): Any {
        return when (value) {
            is Real -> realFunction(value)
            is Complex -> complexFunction(value)
            else -> throw IllegalStateException("Cannot apply function $name to value $value")
        }
    }
}
