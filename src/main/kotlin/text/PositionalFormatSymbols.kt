package garden.ephemeral.calculator.text

data class PositionalFormatSymbols(
    val digits: String = "0123456789↊↋",
    val plus: String = "+",
    val minus: String = "-",
    val radixSeparator: String = ";",
    val infinity: String = "∞",
    val notANumber: String = "NaN",
    val i: String = "i",
) {
    val digitZero: String = digits.substring(0, 1)
    val digitOne: String = digits.substring(1, 2)

    fun digitToChar(digit: Int): Char = digits[digit]

    fun charToDigit(ch: Char): Int = digits.indexOf(ch)
}
