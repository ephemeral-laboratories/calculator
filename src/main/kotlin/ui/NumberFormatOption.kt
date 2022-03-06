package garden.ephemeral.calculator.ui

import com.ibm.icu.text.DecimalFormatSymbols
import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.text.NumberFormats
import garden.ephemeral.calculator.ui.common.Localizable
import java.util.*

enum class NumberFormatOption(
    override val localizedName: String,
    val numberFormatFactory: (String?) -> NumberFormat,
    val defaultRadixSeparator: () -> RadixSeparatorOption,
) : Localizable {
    DECIMAL(
        localizedName = AppStrings.Decimal,
        numberFormatFactory = NumberFormats::createDecimalFormat,
        defaultRadixSeparator = {
            val locale = androidx.compose.ui.text.intl.Locale.current
            val symbols = DecimalFormatSymbols.getInstance(Locale(locale.language, locale.region))
            if (symbols.decimalSeparator == ',') RadixSeparatorOption.COMMA else RadixSeparatorOption.PERIOD
        },
    ),
    DOZENAL(
        localizedName = AppStrings.Dozenal,
        numberFormatFactory = NumberFormats::createDozenalFormat,
        defaultRadixSeparator = { RadixSeparatorOption.SEMICOLON },
    ),
    ;
}
