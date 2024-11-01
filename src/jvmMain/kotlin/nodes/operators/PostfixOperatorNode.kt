package garden.ephemeral.calculator.nodes.operators

import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.calculator.text.ValueFormat

class PostfixOperatorNode(val operator: PostfixOperator, val child: Node) : BaseBranchNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String {
        val symbol = operator.printedSymbol
        val inner = child.prettyPrint(valueFormat)
        return "$inner$symbol"
    }

    override fun evaluate(): Value {
        return Value(operator.apply(child.evaluate().value))
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is PostfixOperatorNode &&
            operator == other.operator &&
            child.isCloseTo(other.child, delta)
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("operator" to operator)

    override fun childrenForToString(): List<Node> = listOf(child)
}
