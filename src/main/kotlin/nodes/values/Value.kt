package garden.ephemeral.calculator.nodes.values

import garden.ephemeral.calculator.nodes.BaseLeafNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.text.ValueFormat
import garden.ephemeral.math.complex.Complex
import kotlin.math.abs

class Value(val value: Any) : BaseLeafNode() {
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
