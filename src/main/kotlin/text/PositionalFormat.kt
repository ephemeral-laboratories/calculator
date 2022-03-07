package garden.ephemeral.calculator.text

import com.ibm.icu.text.NumberFormat
import java.math.BigDecimal
import java.math.BigInteger
import java.text.FieldPosition
import java.text.ParsePosition

/**
 * Generalisation of radix-based positional formatting.
 */
class PositionalFormat(
    private val radix: Int,
    val symbols: PositionalFormatSymbols = PositionalFormatSymbols(),
) : NumberFormat() {
    init {
        require(radix == symbols.digits.length) { "Radix ($radix) and symbols ($symbols.digits) don't match!" }
    }

    override fun format(number: Double, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        requireNotNull(toAppendTo)

        val positiveNumber = if (number < 0.0) {
            toAppendTo.append(symbols.minus)
            -number
        } else {
            number
        }

        if (positiveNumber.isNaN()) {
            toAppendTo.append(symbols.notANumber)
            return toAppendTo
        }
        if (positiveNumber.isInfinite()) {
            toAppendTo.append(symbols.infinity)
            return toAppendTo
        }

        // Format everything before the radix point
        val integerPart = positiveNumber.toLong()
        format(integerPart, toAppendTo, pos)

        // Collect fraction digits
        var remainder = positiveNumber - integerPart
        val fractionDigits = mutableListOf<Int>()
        for (i in 1..maximumFractionDigits) {
            remainder *= radix
            val digit = remainder.toInt()
            fractionDigits.add(digit)
            remainder -= digit
        }

        // Trim training zeroes
        while (fractionDigits.size > minimumFractionDigits) {
            if (fractionDigits[fractionDigits.lastIndex] == 0) {
                fractionDigits.removeLast()
            } else {
                break
            }
        }

        if (minimumFractionDigits > 0 || fractionDigits.isNotEmpty()) {
            toAppendTo.append(symbols.radixSeparator)
        }

        for (digit in fractionDigits) {
            toAppendTo.append(symbols.digitToChar(digit))
        }

        return toAppendTo
    }

    override fun format(number: Long, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        requireNotNull(toAppendTo)
        requireNotNull(pos)

        var positiveNumber = if (number < 0.0) {
            toAppendTo.append(symbols.minus)
            -number
        } else {
            number
        }

        // Collect digits
        val digits = mutableListOf<Int>()
        while (positiveNumber > 0) {
            val digit = (positiveNumber % radix).toInt()
            digits.add(digit)
            positiveNumber /= radix
        }
        while (digits.size < minimumIntegerDigits) {
            digits.add(0)
        }

        // Output in the right order
        for (digit in digits.reversed()) {
            toAppendTo.append(symbols.digitToChar(digit))
        }

        return toAppendTo
    }

    // BigInteger we could conceivably support, I guess.

    override fun format(number: BigInteger?, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer =
        willNotSupport("Formatting BigInteger")

    override fun format(number: BigDecimal?, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer =
        willNotSupport("Formatting BigDecimal")

    override fun format(number: com.ibm.icu.math.BigDecimal?, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer =
        willNotSupport("Formatting BigDecimal")

    private fun willNotSupport(thing: String): Nothing = throw UnsupportedOperationException("$thing not supported")

    override fun parse(text: String?, parsePosition: ParsePosition?): Number {
        requireNotNull(text)
        requireNotNull(parsePosition)

        var integerPart: String
        val fractionPart: String
        val radixSeparatorIndex = text.indexOf(symbols.radixSeparator)
        if (radixSeparatorIndex >= 0) {
            integerPart = text.substring(0, radixSeparatorIndex)
            fractionPart = text.substring(radixSeparatorIndex + 1)
        } else {
            integerPart = text
            fractionPart = ""
        }

        val sign = if (integerPart.startsWith(symbols.minus)) {
            integerPart = integerPart.substring(1)
            -1
        } else {
            if (integerPart.startsWith(symbols.plus)) {
                integerPart = integerPart.substring(1)
            }
            1
        }

        var integer = 0L
        for (ch in integerPart) {
            integer *= radix
            integer += symbols.charToDigit(ch)
        }

        var fraction = 0.0
        var multiplier = 1.0
        for (ch in fractionPart) {
            multiplier /= radix
            fraction += symbols.charToDigit(ch) * multiplier
        }

        // XXX: Do we want to support partial parsing?
        parsePosition.index = text.length

        return sign * integer + fraction
    }
}
