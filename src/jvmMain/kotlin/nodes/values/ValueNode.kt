package garden.ephemeral.calculator.nodes.values

import garden.ephemeral.calculator.nodes.BaseLeafNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.text.ValueFormat
import garden.ephemeral.calculator.values.Value

/**
 * A node containing a value.
 *
 * @property value the contained value.
 */
class ValueNode(val value: Value) : BaseLeafNode() {
    override fun evaluate(): Value {
        return value
    }

    override fun prettyPrint(valueFormat: ValueFormat): String {
        return valueFormat.format(value)
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        if (other !is ValueNode) return false
        return value.isCloseTo(other.value, delta)
    }

    override fun attributesForToString(): Map<String, Any> {
        val valueType = value::class.simpleName
        return mapOf("value" to "$value {$valueType}")
    }
}
