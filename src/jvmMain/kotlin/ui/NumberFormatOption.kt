package garden.ephemeral.calculator.ui

import androidx.compose.runtime.Composable
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.number_format_decimal
import garden.ephemeral.calculator.calculator.generated.resources.number_format_dozenal
import garden.ephemeral.calculator.text.PositionalFormatSymbols
import garden.ephemeral.calculator.ui.common.Localizable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
enum class NumberFormatOption(
    private val stringResourceKey: StringResource,
    val radix: Int,
    val defaultSymbols: PositionalFormatSymbols,
) : Localizable {
    DECIMAL(
        stringResourceKey = Res.string.number_format_decimal,
        radix = 10,
        defaultSymbols = PositionalFormatSymbols(
            digits = "0123456789",
            minus = "-",
            radixSeparator = ".",
        ),
    ),
    DOZENAL(
        stringResourceKey = Res.string.number_format_dozenal,
        radix = 12,
        defaultSymbols = PositionalFormatSymbols(),
    ),
    ;

    override val localizedName: String
        @Composable
        get() = stringResource(stringResourceKey)
}
