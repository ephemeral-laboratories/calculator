package garden.ephemeral.calculator.text

import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.creals.Real
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

    private val realRadix = Real.valueOf(radix)

    @Suppress("WRONG_NULLABILITY_FOR_JAVA_OVERRIDE")
    override fun format(number: Any?, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        if (number is Real) {
            return format(number, toAppendTo, pos)
        } else {
            throw IllegalArgumentException("Unexpected value $number (type ${number?.javaClass}")
        }
    }

    fun format(number: Real, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer {
        requireNotNull(toAppendTo)
        requireNotNull(pos)

        // We should only need 1 more precision, but it turns out that toStringFloatRep() doesn't do proper
        // rounding, whereas our DigitBuffer does. So ask for 2 more instead and just let DigitBuffer
        // round the last digit if it needs to.
        // This behaviour still isn't 100% correct. If you have a number like "1.005000000000000005",
        // it won't know to round up unless you generate all those digits.
        val rep = number.toStringFloatRep(
            pointsOfPrecision = maximumIntegerDigits + maximumFractionDigits + 2,
            radix = radix,
            msdPrecision = -maximumFractionDigits,
        )

        if (rep.sign < 0) {
            toAppendTo.append(symbols.minus)
        }

        val radixPointLocation: Int
        val exponent: Int
        if (rep.exponent <= maximumIntegerDigits) {
            radixPointLocation = rep.exponent
            exponent = 0
        } else {
            radixPointLocation = 1
            exponent = rep.exponent - 1
        }

        val buffer = DigitBuffer(rep.radix)
        rep.mantissaDigits.forEachIndexed { index, digit ->
            if (index == radixPointLocation) {
                if (index == 0) buffer.append(0)
                buffer.markRadixPoint()
            }
            buffer.append(digit)
        }
        buffer.enforceLimits(minimumIntegerDigits, minimumFractionDigits, maximumFractionDigits)
        appendDigits(buffer, toAppendTo)

        if (exponent > 0) {
            toAppendTo.append(symbols.exponent)
            val exponentBuffer = DigitBuffer(rep.radix)
            exponentBuffer.appendInt(exponent)
            appendDigits(exponentBuffer, toAppendTo)
        }

        return toAppendTo
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

    override fun format(number: Double, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer =
        willNotSupport("Formatting Double")

    override fun format(number: Long, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer =
        willNotSupport("Formatting Long")

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
            Real.MINUS_ONE
        } else {
            if (integerPart.startsWith(symbols.plus)) {
                integerPart = integerPart.substring(1)
            }
            Real.ONE
        }

        var integer = Real.ZERO
        for (ch in integerPart) {
            integer *= realRadix
            integer += Real.valueOf(symbols.charToDigit(ch))
        }

        var fraction = Real.ZERO
        var multiplier = Real.ONE
        for (ch in fractionPart) {
            multiplier /= realRadix
            fraction += Real.valueOf(symbols.charToDigit(ch)) * multiplier
        }

        // XXX: Do we want to support partial parsing?
        parsePosition.index = text.length

        return sign * (integer + fraction)
    }
}
