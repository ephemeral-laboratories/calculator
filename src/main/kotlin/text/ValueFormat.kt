package garden.ephemeral.calculator.text

import garden.ephemeral.math.complex.Complex
import java.text.FieldPosition

/**
 * Value format which chooses the right format based on the value type.
 */
class ValueFormat(radix: Int, symbols: PositionalFormatSymbols) {
    var realFormat = PositionalFormat(radix, symbols).apply {
        minimumIntegerDigits = 1
        minimumFractionDigits = 0
        maximumFractionDigits = 10
    }
    private var complexFormat = ComplexFormat(realFormat, symbols)

    fun format(value: Any?): String {
        val builder = StringBuffer()
        format(value, builder)
        return builder.toString()
    }

    fun format(value: Any?, toAppendTo: StringBuffer) {
        when (value) {
            // XXX: Stuck with accepting a StringBuffer because NumberFormat wants it.
            //      Can we just avoid using NumberFormat entirely?
            is Double -> realFormat.format(value, toAppendTo, FieldPosition(0))
            is Complex -> complexFormat.format(value, toAppendTo)
            else -> toAppendTo.append(value.toString())
        }
    }
}
