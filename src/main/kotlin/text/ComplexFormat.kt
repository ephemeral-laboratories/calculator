package garden.ephemeral.calculator.text

import garden.ephemeral.math.complex.Complex
import java.text.Format
import kotlin.math.abs

class ComplexFormat(private var realFormat: Format, private var symbols: PositionalFormatSymbols) {
    /**
     * Formats a complex value.
     *
     * @param value the complex value.
     * @return the formatted value.
     */
    fun format(value: Complex): String {
        val builder = StringBuilder()
        format(value, builder)
        return builder.toString()
    }

    /**
     * Formats a complex value.
     *
     * @param value the complex value.
     * @param toAppendTo the string buffer to append to.
     */
    fun format(value: Complex, toAppendTo: Appendable) {
        if (value.real.isNaN() && value.imaginary.isNaN()) {
            toAppendTo.append(symbols.notANumber)
            return
        }
        if (value.real.isInfinite() || value.imaginary.isInfinite()) {
            toAppendTo.append(symbols.infinity)
            return
        }
        if (value.real.isNaN() || value.imaginary.isNaN()) {
            toAppendTo.append(symbols.notANumber)
            return
        }

        val formattedReal = realFormat.format(abs(value.real))
        val formattedImaginary = realFormat.format(abs(value.imaginary))
        val hasReal = formattedReal != symbols.digitZero
        val hasImaginary = formattedImaginary != symbols.digitZero
        val realIsNegative = hasReal && value.real < 0
        val imaginaryIsNegative = hasImaginary && value.imaginary < 0

        if (!hasReal && !hasImaginary) {
            toAppendTo.append(symbols.digitZero)
            return
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
    }
}
