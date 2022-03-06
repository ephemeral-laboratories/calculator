package garden.ephemeral.calculator.nodes.ops

import garden.ephemeral.math.complex.Complex
import garden.ephemeral.math.complex.toComplex

enum class InfixOperator(
    val printedSymbol: String,
    val realFunction: (Double, Double) -> Double,
    val complexFunction: (Complex, Complex) -> Complex,
) {
    PLUS("+", Double::plus, Complex::plus),
    MINUS("-", Double::minus, Complex::minus),
    TIMES("×", Double::times, Complex::times),
    IMPLICIT_TIMES("", Double::times, Complex::times),
    DIVIDE("÷", Double::div, Complex::div),
    ;

    fun apply(value1: Any, value2: Any): Any {
        return if (value1 is Double && value2 is Double) {
            realFunction(value1, value2)
        } else if (value1 is Double && value2 is Complex) {
            complexFunction(value1.toComplex(), value2)
        } else if (value1 is Complex && value2 is Double) {
            complexFunction(value1, value2.toComplex())
        } else if (value1 is Complex && value2 is Complex) {
            complexFunction(value1, value2)
        } else {
            throw IllegalStateException("Cannot apply function $name to values $value1, $value2")
        }
    }
}
