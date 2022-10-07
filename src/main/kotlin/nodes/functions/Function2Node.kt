package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.calculator.text.ValueFormat
import org.antlr.v4.runtime.Token
import java.text.ParseException

class Function2Node(val function: Function2, val arg1: Node, val arg2: Node) : BaseBranchNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String {
        val name = function.printedName
        val arg1String = arg1.prettyPrint(valueFormat)
        val arg2String = arg2.prettyPrint(valueFormat)
        return "$name($arg1String, $arg2String)"
    }

    override fun evaluate(): Value {
        return Value(function.apply(arg1.evaluate().value, arg2.evaluate().value))
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is Function2Node &&
            function == other.function &&
            arg1.isCloseTo(other.arg1, delta) &&
            arg2.isCloseTo(other.arg2, delta)
    }

    companion object {
        fun create(nameNode: Token, arg1Node: Node, arg2Node: Node): Node {
            val name = nameNode.text
            val offset = nameNode.startIndex
            val function = Function2.findByName(name) ?: throw ParseException("Function not found: $name", offset)
            return Function2Node(function, arg1Node, arg2Node)
        }
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("function" to function)

    override fun childrenForToString(): List<Node> = listOf(arg1, arg2)
}
