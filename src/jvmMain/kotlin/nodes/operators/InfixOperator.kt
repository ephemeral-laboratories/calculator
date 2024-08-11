package garden.ephemeral.calculator.nodes.operators

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.complex.toComplex
import garden.ephemeral.calculator.creals.Real

enum class InfixOperator(
    val printedSymbol: String,
    val realFunction: (Real, Real) -> Real,
    val complexFunction: (Complex, Complex) -> Complex,
) {
    PLUS("+", Real::plus, Complex::plus),
    MINUS("-", Real::minus, Complex::minus),
    TIMES("×", Real::times, Complex::times),
    IMPLICIT_TIMES("", Real::times, Complex::times),
    DIVIDE("÷", Real::div, Complex::div),
    POWER("^", Real::pow, Complex::pow),
    ;

    fun apply(value1: Any, value2: Any): Any {
        return if (value1 is Real && value2 is Real) {
            realFunction(value1, value2)
        } else if (value1 is Real && value2 is Complex) {
            complexFunction(value1.toComplex(), value2)
        } else if (value1 is Complex && value2 is Real) {
            complexFunction(value1, value2.toComplex())
        } else if (value1 is Complex && value2 is Complex) {
            complexFunction(value1, value2)
        } else {
            throw IllegalStateException("Cannot apply function $name to values $value1, $value2")
        }
    }
}
