package garden.ephemeral.calculator.text

data class PositionalFormatSymbols(
    val digits: String = "0123456789↊↋",
    val plus: String = "+",
    val minus: String = "-",
    val radixSeparator: String = ";",
    val infinity: String = "∞",
    val notANumber: String = "NaN",
) {
    fun digitToChar(digit: Int): Char = digits[digit]

    fun charToDigit(ch: Char): Int = digits.indexOf(ch)
}
