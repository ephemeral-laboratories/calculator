package garden.ephemeral.calculator.nodes

import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.nodes.values.Value

class Parentheses(private val inner: Node) : BaseBranchNode() {
    override fun prettyPrint(numberFormat: NumberFormat): String {
        val innerString = inner.prettyPrint(numberFormat)
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
