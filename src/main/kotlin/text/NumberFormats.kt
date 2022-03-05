package garden.ephemeral.calculator.text
import com.ibm.icu.text.DecimalFormat
import com.ibm.icu.text.DecimalFormatSymbols
import com.ibm.icu.text.NumberFormat

object NumberFormats {
    val dozenalFormat: NumberFormat = DozenalFormat().apply {
        minimumIntegerDigits = 1
        minimumFractionDigits = 0
        maximumFractionDigits = 10
    }

    val decimalFormat: NumberFormat = NumberFormat.getNumberInstance().apply {
        minimumIntegerDigits = 1
        minimumFractionDigits = 0
        maximumFractionDigits = 12
    }

    fun createDozenalFormat(customRadixSeparator: Char?): NumberFormat {
        return DozenalFormat().apply {
            minimumIntegerDigits = 1
            minimumFractionDigits = 0
            maximumFractionDigits = 10
            if (customRadixSeparator != null) {
                radixSeparator = customRadixSeparator
            }
        }
    }

    fun createDecimalFormat(customSeparator: Char?): NumberFormat {
        val symbols = DecimalFormatSymbols.getInstance()
        if (customSeparator != null) {
            symbols.decimalSeparator = customSeparator
        }
        return DecimalFormat("0.#", symbols).apply {
            minimumIntegerDigits = 1
            minimumFractionDigits = 0
            maximumFractionDigits = 12
        }
    }
}
