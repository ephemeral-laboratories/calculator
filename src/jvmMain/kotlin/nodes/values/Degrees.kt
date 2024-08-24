package garden.ephemeral.calculator.nodes.values

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.text.ValueFormat

private val RADIANS_PER_DEGREE = Real.TAU / Real.valueOf(360)

class Degrees(val degrees: Value) : BaseBranchNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String {
        return degrees.prettyPrint(valueFormat) + "°"
    }

    override fun evaluate() = when (val result = degrees.evaluate().value) {
        is Real -> Value(result * RADIANS_PER_DEGREE)
        is Complex -> Value(result * RADIANS_PER_DEGREE)
        else -> throw IllegalArgumentException("No way to evaluate $result (${result.javaClass}")
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        if (other !is Degrees) return false
        return degrees.isCloseTo(other.degrees, delta)
    }

    override fun attributesForToString(): Map<String, Any> {
        return emptyMap()
    }

    override fun childrenForToString(): List<Node> {
        return listOf(degrees)
    }
}
