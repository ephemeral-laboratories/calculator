package garden.ephemeral.calculator.nodes.ops

import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.values.Value

class PrefixOperatorNode(val operator: PrefixOperator, val child: Node) : BaseBranchNode() {
    override fun prettyPrint(numberFormat: NumberFormat): String {
        return "${operator.printedSymbol}${child.prettyPrint(numberFormat)}"
    }

    override fun evaluate(): Value {
        return Value(operator.apply(child.evaluate().value))
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is PrefixOperatorNode &&
            operator == other.operator &&
            child.isCloseTo(other.child, delta)
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("operator" to operator)

    override fun childrenForToString(): List<Node> = listOf(child)
}
