package garden.ephemeral.calculator.ui

import garden.ephemeral.calculator.ui.common.Localizable

enum class RadixSeparatorOption(
    override val localizedName: String,
    val symbol: Char? = null,
) : Localizable {
    PERIOD(AppStrings.Period, '.'),
    COMMA(AppStrings.Comma, ','),
    SEMICOLON(AppStrings.Semicolon, ';'),
}
