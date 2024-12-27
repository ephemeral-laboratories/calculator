package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real

class ComplexFormat(private val realFormat: PositionalFormat, private val symbols: PositionalFormatSymbols) {
    /**
     * Formats a complex value.
     *
     * @param value the complex value.
     * @return the formatted value.
     */
    fun format(value: Complex) = buildString {
        val absReal = garden.ephemeral.calculator.creals.abs(value.real)
        val absImag = garden.ephemeral.calculator.creals.abs(value.imag)

        val formattedReal = realFormat.format(absReal)
        val formattedImaginary = realFormat.format(absImag)

        val realSignum = value.real.signum(-realFormat.maximumFractionDigits)
        val imagSignum = value.imag.signum(-realFormat.maximumFractionDigits)

        val hasReal = realSignum != 0
        val hasImaginary = imagSignum != 0
        val realIsNegative = realSignum < 0
        val imaginaryIsNegative = imagSignum < 0
        val imaginaryIsOne = absImag.compareTo(Real.ONE, -realFormat.maximumFractionDigits) == 0

        if (!hasReal && !hasImaginary) {
            append(symbols.digitZero)
            return@buildString
        }

        if (hasReal) {
            if (realIsNegative) {
                append(symbols.minus)
            }
            append(formattedReal)
            if (hasImaginary) {
                val symbol = if (imaginaryIsNegative) symbols.minus else symbols.plus
                append(" $symbol ")
            }
        }

        if (hasImaginary) {
            if (imaginaryIsNegative && !hasReal) {
                append(symbols.minus)
            }
            if (!imaginaryIsOne) {
                append(formattedImaginary)
            }
            append(symbols.i)
        }
    }
}
