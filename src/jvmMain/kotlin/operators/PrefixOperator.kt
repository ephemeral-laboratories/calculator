package garden.ephemeral.calculator.operators

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.values.Value

enum class PrefixOperator(
    val printedSymbol: String,
    val realFunction: (Real) -> Real,
    val complexFunction: (Complex) -> Complex,
) {
    UNARY_MINUS("-", Real::unaryMinus, Complex::unaryMinus),
    ;

    fun apply(value: Value): Value = when (value) {
        is Value.OfReal -> Value.OfReal(realFunction(value.value))
        is Value.OfComplex -> Value.OfComplex(complexFunction(value.value))
    }
}