package garden.ephemeral.calculator.functions

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.values.Value

enum class Function2(
    val printedName: String,
    val realFunction: (Real, Real) -> Value,
    val complexFunction: (Complex, Complex) -> Value,
) {
    POW(
        "pow",
        { base, exponent -> Value.OfReal(base.pow(exponent)) },
        { base, exponent -> Value.OfComplex(base.pow(exponent)) },
    ),
    ;

    operator fun invoke(value1: Value, value2: Value): Value = if (value1 is Value.OfReal && value2 is Value.OfReal) {
        realFunction(value1.value, value2.value)
    } else {
        complexFunction(value1.complexValue, value2.complexValue)
    }

    companion object {
        private val byName = entries.associateBy(Function2::printedName)

        fun findByName(name: String): Function2? = byName[name]
    }
}
