package garden.ephemeral.calculator.ui

import garden.ephemeral.calculator.text.PositionalFormatSymbols
import garden.ephemeral.calculator.ui.common.Localizable

enum class NumberFormatOption(
    override val localizedName: String,
    val radix: Int,
    val defaultSymbols: PositionalFormatSymbols,
) : Localizable {
    DECIMAL(
        localizedName = AppStrings.Decimal,
        radix = 10,
        defaultSymbols = PositionalFormatSymbols(
            digits = "0123456789",
            minus = "-",
            radixSeparator = ".",
        ),
    ),
    DOZENAL(
        localizedName = AppStrings.Dozenal,
        radix = 12,
        defaultSymbols = PositionalFormatSymbols(),
    ),
    ;
}
