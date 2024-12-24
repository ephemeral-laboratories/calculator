package garden.ephemeral.calculator.nodes.values

import garden.ephemeral.calculator.nodes.BaseLeafNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.text.ValueFormat
import garden.ephemeral.calculator.values.Constant
import garden.ephemeral.calculator.values.Value

class ConstantNode(val constant: Constant) : BaseLeafNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String = constant.printedName

    override fun evaluate(): Value = constant.value

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is ConstantNode &&
            constant == other.constant
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("constant" to constant)
}
