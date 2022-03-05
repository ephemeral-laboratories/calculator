package garden.ephemeral.calculator.text
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
}
