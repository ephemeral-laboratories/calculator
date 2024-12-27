package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.creals.Real

/**
 * Generalisation of radix-based positional formatting.
 */
class PositionalFormat(
    private val radix: Int,
    val symbols: PositionalFormatSymbols = PositionalFormatSymbols(),
    val minimumIntegerDigits: Int = 1,
    val maximumIntegerDigits: Int = 40,
    val minimumFractionDigits: Int = 0,
    val maximumFractionDigits: Int = 3,
) {
    init {
        require(radix == symbols.digits.length) { "Radix ($radix) and symbols ($symbols.digits) don't match!" }
        require(maximumIntegerDigits >= minimumIntegerDigits)
        require(maximumFractionDigits >= minimumFractionDigits)
    }

    private val realRadix = Real.valueOf(radix)

    /**
     * Formats a [Real] to a [String].
     *
     * @param number the [Real] to format.
     * @return the formatted [String].
     */
    fun format(number: Real): String {
        return format(number, StringBuilder()).toString()
    }

    /**
     * Formats a [Real] to a [StringBuilder].
     * This alternative version could be useful in situations where you wish to reuse a builder.
     *
     * @param number the [Real] to format.
     * @param toAppendTo the builder to append to.
     * @return the builder containing the formatted result. Currently, this is always the same
     *         builder you passed in, but I'm not sure that's safe to assume.
     */
    fun format(number: Real, toAppendTo: StringBuilder): StringBuilder {
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

    /**
     * Parses a [Real] from a [String].
     *
     * @param text the text containing the value to parse.
     * @return the parsed [Real].
     * @throws ParseException if the string did not contain a valid number.
     */
    fun parse(text: String): Real {
        return when (val result = parseSafely(text)) {
            is RealParseResult.Success -> result.parsedValue
            is RealParseResult.Failure ->
                throw ParseException(message = "Unparseable number: \"$text\"", errorOffset = result.errorIndex)
        }
    }

    /**
     * Parses a [Real] from a [String].
     * In many cases, it's reasonable to assume that an error _can_ occur when parsing strings
     * provided by the end user.  This alternative method can be used when you don't want to
     * use an exception as flow control.
     *
     * @param text the text containing the value to parse.
     * @return a result object containing the result of the operation, which will either be a
     *         [RealParseResult.Success] or [RealParseResult.Failure].
     */
    fun parseSafely(text: String): RealParseResult {
        var integerPart: String
        var integerPartOffset = 0
        val fractionPart: String
        val fractionPartOffset: Int
        val radixSeparatorIndex = text.indexOf(symbols.radixSeparator)
        if (radixSeparatorIndex >= 0) {
            integerPart = text.substring(0, radixSeparatorIndex)
            fractionPart = text.substring(radixSeparatorIndex + 1)
            fractionPartOffset = radixSeparatorIndex + 1
        } else {
            integerPart = text
            fractionPart = ""
            fractionPartOffset = integerPart.length
        }

        val sign = if (integerPart.startsWith(symbols.minus)) {
            integerPart = integerPart.substring(1)
            integerPartOffset += 1
            Real.MINUS_ONE
        } else {
            if (integerPart.startsWith(symbols.plus)) {
                integerPart = integerPart.substring(1)
                integerPartOffset += 1
            }
            Real.ONE
        }

        var integer = Real.ZERO
        integerPart.forEachIndexed { index, ch ->
            val digit = symbols.tryCharToDigit(ch)
                ?: return RealParseResult.Failure(index = 0, errorIndex = index + integerPartOffset)

            integer *= realRadix
            integer += Real.valueOf(digit)
        }

        var fraction = Real.ZERO
        var multiplier = Real.ONE
        fractionPart.forEachIndexed { index, ch ->
            val digit = symbols.tryCharToDigit(ch)
                ?: return RealParseResult.Failure(index = 0, errorIndex = index + fractionPartOffset)

            multiplier /= realRadix
            fraction += Real.valueOf(digit) * multiplier
        }

        // XXX: Do we want to support partial parsing?
        return RealParseResult.Success(index = text.length, parsedValue = sign * (integer + fraction))
    }
}
