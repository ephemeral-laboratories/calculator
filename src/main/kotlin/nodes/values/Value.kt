package garden.ephemeral.calculator.nodes.values

import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.nodes.BaseLeafNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.math.complex.Complex
import kotlin.math.abs

class Value(val value: Any) : BaseLeafNode() {

    override fun evaluate(): Value {
        return this
    }

    override fun prettyPrint(numberFormat: NumberFormat): String {
        return when (value) {
            is Double -> {
                if (value.isNaN()) {
                    "undefined"
                } else {
                    numberFormat.format(value)
                }
            }
            is Complex -> {
                if (value.real.isNaN() || value.imaginary.isNaN()) {
                    "undefined"
                } else {
                    formatComplex(value, numberFormat)
                }
            }
            else -> value.toString()
        }
    }

    private fun formatComplex(value: Complex, numberFormat: NumberFormat): String {
        return buildString {
            val formattedReal = numberFormat.format(abs(value.real))
            val formattedImaginary = numberFormat.format(abs(value.imaginary))
            val hasReal = formattedReal != "0"
            val hasImaginary = formattedImaginary != "0"
            val realIsNegative = hasReal && value.real < 0
            val imaginaryIsNegative = hasImaginary && value.imaginary < 0

            if (!hasReal && !hasImaginary) {
                return "0"
            }

            if (hasReal) {
                if (realIsNegative) {
                    append('-')
                }
                append(formattedReal)
                if (hasImaginary) {
                    append(if (imaginaryIsNegative) " - " else " + ")
                }
            }

            if (hasImaginary) {
                if (imaginaryIsNegative && !hasReal) {
                    append('-')
                }
                if (formattedImaginary != "1") {
                    append(formattedImaginary)
                }
                append('i')
            }
        }
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        if (other !is Value) return false
        val otherValue = other.value
        return when (value) {
            is Double -> otherValue is Double && abs(value - otherValue) < delta
            is Complex -> otherValue is Complex && (value - otherValue).norm < delta
            else -> throw IllegalArgumentException("No way to compare $value with $otherValue")
        }
    }

    override fun attributesForToString(): Map<String, Any> {
        val valueType = value::class.simpleName
        return mapOf("value" to "$value {$valueType}")
    }
}
