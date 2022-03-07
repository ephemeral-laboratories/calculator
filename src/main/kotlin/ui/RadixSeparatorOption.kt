package garden.ephemeral.calculator.ui

import garden.ephemeral.calculator.ui.common.Localizable

enum class RadixSeparatorOption(
    override val localizedName: String,
    val symbol: String,
) : Localizable {
    PERIOD(AppStrings.Period, "."),
    COMMA(AppStrings.Comma, ","),
    SEMICOLON(AppStrings.Semicolon, ";"),
    ;

    companion object {
        fun forSymbol(symbol: String): RadixSeparatorOption {
            return when (symbol) {
                "." -> PERIOD
                "," -> COMMA
                ";" -> SEMICOLON
                else -> throw IllegalArgumentException("Unknown symbol: $symbol")
            }
        }

        fun defaultFor(numberFormatOption: NumberFormatOption): RadixSeparatorOption {
            return forSymbol(numberFormatOption.defaultSymbols.radixSeparator)
        }
    }
}
