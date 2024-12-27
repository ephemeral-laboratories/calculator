package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.calculator.functions.Function2
import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.values.Value
import garden.ephemeral.calculator.text.ValueFormat

class Function2Node(val function: Function2, val arg1: Node, val arg2: Node) : BaseBranchNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String {
        val name = function.printedName
        val arg1String = arg1.prettyPrint(valueFormat)
        val arg2String = arg2.prettyPrint(valueFormat)
        return "$name($arg1String, $arg2String)"
    }

    override fun evaluate(): Value {
        return function(arg1.evaluate(), arg2.evaluate())
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is Function2Node &&
            function == other.function &&
            arg1.isCloseTo(other.arg1, delta) &&
            arg2.isCloseTo(other.arg2, delta)
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("function" to function)

    override fun childrenForToString(): List<Node> = listOf(arg1, arg2)
}
