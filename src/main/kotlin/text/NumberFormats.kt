package garden.ephemeral.calculator.text

import com.ibm.icu.text.NumberFormat

object NumberFormats {
    private val dozenalSymbols by lazy { PositionalFormatSymbols() }
    private val decimalSymbols by lazy {
        PositionalFormatSymbols(
            digits = "0123456789",
            minus = "-",
            radixSeparator = ".",
        )
    }

    fun createDozenalFormat(customRadixSeparator: String? = null): NumberFormat {
        val symbols = if (customRadixSeparator != null) {
            dozenalSymbols.copy(radixSeparator = customRadixSeparator)
        } else {
            dozenalSymbols
        }

        return PositionalFormat(12, symbols).apply {
            minimumIntegerDigits = 1
            minimumFractionDigits = 0
            maximumFractionDigits = 10
        }
    }

    fun createDecimalFormat(customRadixSeparator: String? = null): NumberFormat {
        val symbols = if (customRadixSeparator != null) {
            decimalSymbols.copy(radixSeparator = customRadixSeparator)
        } else {
            decimalSymbols
        }

        return PositionalFormat(10, symbols).apply {
            minimumIntegerDigits = 1
            minimumFractionDigits = 0
            maximumFractionDigits = 12
        }
    }
}
