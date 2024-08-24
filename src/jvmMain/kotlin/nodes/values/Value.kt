package garden.ephemeral.calculator.nodes.values

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.abs
import garden.ephemeral.calculator.nodes.BaseLeafNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.text.ValueFormat

class Value internal constructor(value: Any) : BaseLeafNode() {

    /**
     * The contained value.
     */
    val value = when (value) {
        is Double -> Real.valueOf(value)
        is Real -> value
        is Complex -> value
        else -> throw IllegalArgumentException("Unexpected value $value (${value.javaClass}")
    }

    override fun evaluate(): Value {
        return this
    }

    override fun prettyPrint(valueFormat: ValueFormat): String {
        return valueFormat.format(value)
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        if (other !is Value) return false
        val otherValue = other.value
        return when (value) {
            is Real -> otherValue is Real && abs(value - otherValue).toDouble() < delta
            is Complex -> otherValue is Complex && (value - otherValue).norm.toDouble() < delta
            else -> throw IllegalArgumentException("No way to compare $value (${value.javaClass}) with $otherValue (${otherValue.javaClass})")
        }
    }

    override fun attributesForToString(): Map<String, Any> {
        val valueType = value::class.simpleName
        return mapOf("value" to "$value {$valueType}")
    }
}

fun Value(value: Real) = Value(value as Any)
fun Value(value: Complex) = Value(value as Any)
