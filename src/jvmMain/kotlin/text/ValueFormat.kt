package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real

/**
 * Value format which chooses the right format based on the value type.
 */
class ValueFormat(private val radix: Int, private val symbols: PositionalFormatSymbols) {
    val realFormat = PositionalFormat(radix = radix, symbols = symbols, maximumFractionDigits = 10)
    private val complexFormat = ComplexFormat(realFormat = realFormat, symbols = symbols)

    fun format(value: Any?): String = when (value) {
        is Real -> formatReal(value)
        is Complex -> formatComplex(value)
        else -> value.toString()
    }

    private fun formatReal(value: Real) = realFormat.format(value)

    private fun formatComplex(value: Complex) = complexFormat.format(value)
}
