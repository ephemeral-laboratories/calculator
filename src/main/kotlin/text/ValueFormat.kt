package garden.ephemeral.calculator.text

import garden.ephemeral.math.complex.Complex
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition

/**
 * Value format which chooses the right format based on the value type.
 */
class ValueFormat(radix: Int, symbols: PositionalFormatSymbols) : Format() {
    private var realFormat = PositionalFormat(radix, symbols)
    private var complexFormat = ComplexFormat(realFormat, symbols)

    override fun format(value: Any?, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
        return when (value) {
            is Double -> realFormat.format(value, toAppendTo, pos)
            is Complex -> complexFormat.format(value, toAppendTo)
            else -> return toAppendTo.append(value.toString())
        }
    }

    fun parse(source: String): Double {
        return parseObject(source, ParsePosition(0)) as Double
    }

    override fun parseObject(source: String?, pos: ParsePosition): Any {
        // XXX: Only supports parsing reals at the moment because complex numbers are
        //      handled by ExpressionParser.
        return realFormat.parseObject(source, pos)
    }
}
