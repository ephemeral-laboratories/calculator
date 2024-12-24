package garden.ephemeral.calculator.operators

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.values.Value

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

    fun apply(value1: Value, value2: Value): Value = if (value1 is Value.OfReal && value2 is Value.OfReal) {
        Value.OfReal(realFunction(value1.value, value2.value))
    } else {
        Value.OfComplex(complexFunction(value1.complexValue, value2.complexValue))
    }
}