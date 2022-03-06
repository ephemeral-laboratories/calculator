package garden.ephemeral.calculator.text

import com.ibm.icu.text.NumberFormat
import java.math.BigDecimal
import java.math.BigInteger
import java.text.FieldPosition
import java.text.ParsePosition

class DozenalFormat : NumberFormat() {
    // XXX: It would be better to have something akin to DecimalFormatSymbols,
    //      but we'll get there next step!
    var radixSeparator = ';'

    override fun format(number: Double, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        requireNotNull(toAppendTo)

        val positiveNumber = if (number < 0.0) {
            toAppendTo.append(MINUS)
            -number
        } else {
            number
        }

        // Format everything before the radix point
        val integerPart = positiveNumber.toLong()
        format(integerPart, toAppendTo, pos)

        // Collect fraction digits
        var remainder = positiveNumber - integerPart
        val fractionDigits = mutableListOf<Int>()
        for (i in 1..maximumFractionDigits) {
            remainder *= RADIX
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
            toAppendTo.append(radixSeparator)
        }

        for (digit in fractionDigits) {
            toAppendTo.append(digitToChar(digit))
        }

        return toAppendTo
    }

    override fun format(number: Long, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        requireNotNull(toAppendTo)
        requireNotNull(pos)

        var positiveNumber = if (number < 0.0) {
            toAppendTo.append(MINUS)
            -number
        } else {
            number
        }

        // Collect digits
        val digits = mutableListOf<Int>()
        while (positiveNumber > 0) {
            val digit = (positiveNumber % RADIX).toInt()
            digits.add(digit)
            positiveNumber /= RADIX
        }
        while (digits.size < minimumIntegerDigits) {
            digits.add(0)
        }

        // Output in the right order
        for (digit in digits.reversed()) {
            toAppendTo.append(digitToChar(digit))
        }

        return toAppendTo
    }

    override fun format(number: BigInteger?, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        throw UnsupportedOperationException("Formatting BigInteger not yet supported")
    }

    override fun format(number: BigDecimal?, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        throw UnsupportedOperationException("Formatting BigDecimal not yet supported")
    }

    override fun format(
        number: com.ibm.icu.math.BigDecimal?,
        toAppendTo: StringBuffer?,
        pos: FieldPosition?
    ): StringBuffer {
        throw UnsupportedOperationException("Formatting BigDecimal not yet supported")
    }

    override fun parse(text: String?, parsePosition: ParsePosition?): Number {
        requireNotNull(text)
        requireNotNull(parsePosition)

        var integerPart: String
        val fractionPart: String
        val radixSeparatorIndex = text.indexOf(radixSeparator)
        if (radixSeparatorIndex >= 0) {
            integerPart = text.substring(0, radixSeparatorIndex)
            fractionPart = text.substring(radixSeparatorIndex + 1)
        } else {
            integerPart = text
            fractionPart = ""
        }

        val sign = if (integerPart.startsWith(MINUS)) {
            integerPart = integerPart.substring(1)
            -1
        } else {
            1
        }

        var integer = 0L
        for (ch in integerPart) {
            integer *= RADIX
            integer += charToDigit(ch)
        }

        var fraction = 0.0
        var multiplier = 1.0
        for (ch in fractionPart) {
            multiplier /= RADIX
            fraction += charToDigit(ch) * multiplier
        }

        // XXX: Do we want to support partial parsing?
        parsePosition.index = text.length

        return sign * integer + fraction
    }

    private fun digitToChar(digit: Int): Char {
        return DIGITS[digit]
    }

    private fun charToDigit(ch: Char): Int {
        return DIGITS.indexOf(ch)
    }

    companion object {
        const val RADIX = 12
        const val MINUS = '-'
        const val DIGITS = "0123456789↊↋"
    }
}
