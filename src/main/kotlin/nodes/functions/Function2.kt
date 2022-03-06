package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.math.complex.Complex
import garden.ephemeral.math.complex.toComplex
import kotlin.math.pow

enum class Function2(
    val printedName: String,
    val realFunction: (Double, Double) -> Any,
    val complexFunction: (Complex, Complex) -> Any,
) {
    POW("pow", Double::pow, Complex::pow),
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

    companion object {
        private val byName = values()
            .asSequence()
            .map { f -> f.printedName to f }
            .toMap()

        fun findByName(name: String): Function2? = byName[name]
    }
}
