package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.values.Value
import garden.ephemeral.calculator.text.ValueFormat

class Parentheses(private val inner: Node) : BaseBranchNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String {
        val innerString = inner.prettyPrint(valueFormat)
        return "($innerString)"
    }

    override fun evaluate(): Value {
        return inner.evaluate()
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is Parentheses &&
            inner.isCloseTo(other.inner, delta)
    }

    override fun attributesForToString(): Map<String, Any> = mapOf()

    override fun childrenForToString(): List<Node> = listOf(inner)
}
