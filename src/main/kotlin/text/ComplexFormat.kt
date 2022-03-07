package garden.ephemeral.calculator.text

import garden.ephemeral.math.complex.Complex
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import kotlin.math.abs

class ComplexFormat(private var realFormat: Format, private var symbols: PositionalFormatSymbols) : Format() {
    override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition?): StringBuffer {
        require(obj is Complex) { "Only supports Complex, got: $obj" }

        return format(obj, toAppendTo)
    }

    fun format(value: Complex, toAppendTo: StringBuffer): StringBuffer {
        if (value.real.isNaN() && value.imaginary.isNaN()) {
            return toAppendTo.append(symbols.notANumber)
        }
        if (value.real.isInfinite() || value.imaginary.isInfinite()) {
            return toAppendTo.append(symbols.infinity)
        }
        if (value.real.isNaN() || value.imaginary.isNaN()) {
            return toAppendTo.append(symbols.notANumber)
        }

        val formattedReal = realFormat.format(abs(value.real))
        val formattedImaginary = realFormat.format(abs(value.imaginary))
        val hasReal = formattedReal != symbols.digitZero
        val hasImaginary = formattedImaginary != symbols.digitZero
        val realIsNegative = hasReal && value.real < 0
        val imaginaryIsNegative = hasImaginary && value.imaginary < 0

        if (!hasReal && !hasImaginary) {
            return toAppendTo.append(symbols.digitZero)
        }

        if (hasReal) {
            if (realIsNegative) {
                toAppendTo.append(symbols.minus)
            }
            toAppendTo.append(formattedReal)
            if (hasImaginary) {
                val symbol = if (imaginaryIsNegative) symbols.minus else symbols.plus
                toAppendTo.append(" $symbol ")
            }
        }

        if (hasImaginary) {
            if (imaginaryIsNegative && !hasReal) {
                toAppendTo.append(symbols.minus)
            }
            if (formattedImaginary != symbols.digitOne) {
                toAppendTo.append(formattedImaginary)
            }
            toAppendTo.append(symbols.i)
        }

        return toAppendTo
    }

    override fun parseObject(source: String?, pos: ParsePosition): Any {
        throw UnsupportedOperationException("Parse not supported (handled elsewhere)")
    }
}
