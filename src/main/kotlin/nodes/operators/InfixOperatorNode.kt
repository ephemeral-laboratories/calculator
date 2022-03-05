package garden.ephemeral.calculator.nodes.ops

import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.values.Value

class InfixOperatorNode(
    private val operator: InfixOperator,
    private val first: Node,
    private val second: Node,
) : BaseBranchNode() {
    override fun prettyPrint(numberFormat: NumberFormat): String {
        val firstPart = first.prettyPrint(numberFormat)
        val secondPart = second.prettyPrint(numberFormat)
        val symbol = operator.printedSymbol
        return if (symbol.isEmpty()) {
            "$firstPart$secondPart"
        } else {
            "$firstPart $symbol $secondPart"
        }
    }

    override fun evaluate(): Value {
        return Value(operator.apply(first.evaluate().value, second.evaluate().value))
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is InfixOperatorNode &&
                operator == other.operator &&
                first.isCloseTo(other.first, delta) &&
                second.isCloseTo(other.second, delta)
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("operator" to operator)

    override fun childrenForToString(): List<Node> = listOf(first, second)
}