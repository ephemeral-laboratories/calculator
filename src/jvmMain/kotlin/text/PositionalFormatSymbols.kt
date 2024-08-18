package garden.ephemeral.calculator.text

/**
 * Bundle of various symbols used by [PositionalFormat] when formatting or parsing.
 *
 * Similar in purpose to `DecimalFormatSymbols` in the JDK, but we're not limited
 * to decimal formats.
 */
data class PositionalFormatSymbols(
    val digits: String = "0123456789↊↋",
    val plus: String = "+",
    val minus: String = "-",
    val radixSeparator: String = ";",
    val infinity: String = "∞",
    val notANumber: String = "NaN",
    val i: String = "i",
    val exponent: String = "E",
) {
    /**
     * Convenience accessor for digit 0.
     */
    val digitZero: String = digits.substring(0, 1)

    /**
     * Convenience accessor for digit 1.
     */
    val digitOne: String = digits.substring(1, 2)

    /**
     * Formats a single digit as a char.
     *
     * @param digit the digit.
     * @return the char for that digit.
     */
    fun digitToChar(digit: Int): Char = digits[digit]

    /**
     * Attempts to interpret a char as a digit.
     *
     * @param ch the input char.
     * @return the digit, if it was valid, `null` otherwise.
     */
    fun tryCharToDigit(ch: Char): Int? = digits.indexOf(ch).takeIf { it != -1 }
}
