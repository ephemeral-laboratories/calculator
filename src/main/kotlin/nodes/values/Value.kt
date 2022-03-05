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
            is Double -> numberFormat.format(value)
            is Complex -> formatComplex(value, numberFormat)
            else -> value.toString()
        }
    }

    private fun formatComplex(value: Complex, numberFormat: NumberFormat): String {
        return buildString {
            if (value.real != 0.0 || value.imaginary == 0.0) {
                append(numberFormat.format(value.real))
            }

            var imaginary = value.imaginary
            if (value.real != 0.0 && imaginary != 0.0) {
                if (imaginary < 0.0) {
                    append(" - ")
                    imaginary = -imaginary
                } else {
                    append(" + ")
                }
            }

            if (value.imaginary != 0.0) {
                if (imaginary != 1.0) {
                    append(numberFormat.format(imaginary))
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