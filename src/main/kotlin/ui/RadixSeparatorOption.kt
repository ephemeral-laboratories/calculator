package garden.ephemeral.calculator.ui

import androidx.compose.runtime.Composable
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.radix_separator_comma
import garden.ephemeral.calculator.calculator.generated.resources.radix_separator_period
import garden.ephemeral.calculator.calculator.generated.resources.radix_separator_semicolon
import garden.ephemeral.calculator.ui.common.Localizable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
enum class RadixSeparatorOption(
    private val stringResourceKey: StringResource,
    val symbol: String,
) : Localizable {
    PERIOD(Res.string.radix_separator_period, "."),
    COMMA(Res.string.radix_separator_comma, ","),
    SEMICOLON(Res.string.radix_separator_semicolon, ";"),
    ;

    override val localizedName: String
        @Composable
        get() = stringResource(stringResourceKey)

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
