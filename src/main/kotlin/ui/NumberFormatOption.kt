package garden.ephemeral.calculator.ui

import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.text.NumberFormats
import garden.ephemeral.calculator.ui.common.Localizable

enum class NumberFormatOption(
    override val localizedName: String,
    val numberFormatFactory: (Char?) -> NumberFormat,
): Localizable {
    // TODO: i18n
    DECIMAL("Decimal", NumberFormats::createDecimalFormat),
    DOZENAL("Dozenal", NumberFormats::createDozenalFormat),
    ;
}