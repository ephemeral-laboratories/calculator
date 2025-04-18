package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.calculator.functions.Function1
import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.text.ValueFormat
import garden.ephemeral.calculator.values.Value

class Function1Node(val function: Function1, val arg: Node) : BaseBranchNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String {
        val name = function.printedName
        val argString = arg.prettyPrint(valueFormat)
        return "$name($argString)"
    }

    override fun evaluate(): Value {
        return function(arg.evaluate())
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is Function1Node &&
            function == other.function &&
            arg.isCloseTo(other.arg, delta)
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("function" to function)

    override fun childrenForToString(): List<Node> = listOf(arg)
}
