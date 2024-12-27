package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.values.Value

/**
 * Value format which chooses the right format based on the value type.
 */
class ValueFormat(private val radix: Int, private val symbols: PositionalFormatSymbols) {
    val realFormat = PositionalFormat(radix = radix, symbols = symbols, maximumFractionDigits = 10)
    private val complexFormat = ComplexFormat(realFormat = realFormat, symbols = symbols)

    fun format(value: Value): String = when (value) {
        is Value.OfReal -> realFormat.format(value.value)
        is Value.OfComplex -> complexFormat.format(value.value)
    }
}
