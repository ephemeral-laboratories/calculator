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

        val buffer = DigitBuffer(radix)
        fillDigits(positiveNumber, buffer)
        appendDigits(buffer, toAppendTo)

        return toAppendTo
    }

    override fun format(number: Long, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        requireNotNull(toAppendTo)
        requireNotNull(pos)

        val positiveNumber = if (number < 0.0) {
            toAppendTo.append(symbols.minus)
            -number
        } else {
            number
        }

        val buffer = DigitBuffer(radix)
        fillDigits(positiveNumber, buffer)
        appendDigits(buffer, toAppendTo)

        return toAppendTo
    }

    private fun fillDigits(value: Double, buffer: DigitBuffer) {
        val integerPart = value.toLong()

        fillDigits(integerPart, buffer)

        buffer.markRadixPoint()

        var remainder = value - integerPart
        repeat(maximumFractionDigits + 1) {
            remainder *= radix
            val digit = remainder.toInt()
            buffer.append(digit)
            remainder -= digit
        }

        buffer.enforceLimits(minimumIntegerDigits, minimumFractionDigits, maximumFractionDigits)
    }

    private fun fillDigits(value: Long, buffer: DigitBuffer) {
        var remainingIntegerPart = value
        while (remainingIntegerPart > 0) {
            val digit = remainingIntegerPart % radix
            val stillToProcess = remainingIntegerPart / radix
            buffer.prepend(digit.toInt())
            remainingIntegerPart = stillToProcess
        }
    }

    private fun appendDigits(buffer: DigitBuffer, destination: Appendable) {
        buffer.integerDigits
            .map(symbols::digitToChar)
            .forEach(destination::append)

        if (buffer.fractionDigits.isNotEmpty()) {
            destination.append(symbols.radixSeparator)

            buffer.fractionDigits
                .map(symbols::digitToChar)
                .forEach(destination::append)
        }
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

        return sign * (integer + fraction)
    }
}
