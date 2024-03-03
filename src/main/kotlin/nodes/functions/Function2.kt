package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.complex.toComplex
import garden.ephemeral.calculator.creals.Real

enum class Function2(
    val printedName: String,
    val realFunction: (Real, Real) -> Any,
    val complexFunction: (Complex, Complex) -> Any,
) {
    POW("pow", Real::pow, Complex::pow),
    ;

    operator fun invoke(value1: Any, value2: Any): Any {
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

    companion object {
        private val byName = entries.associateBy(Function2::printedName)

        fun findByName(name: String): Function2? = byName[name]
    }
}
