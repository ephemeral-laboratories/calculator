package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.calculator.nodes.BaseBranchNode
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.calculator.text.ValueFormat
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.text.ParseException

class Function1Node(val function: Function1, val arg: Node) : BaseBranchNode() {
    override fun prettyPrint(valueFormat: ValueFormat): String {
        val name = function.printedName
        val argString = arg.prettyPrint(valueFormat)
        return "$name($argString)"
    }

    override fun evaluate(): Value {
        return Value(function.apply(arg.evaluate().value))
    }

    override fun isCloseTo(other: Node, delta: Double): Boolean {
        return other is Function1Node &&
            function == other.function &&
            arg.isCloseTo(other.arg, delta)
    }

    companion object {
        fun create(nameNode: ParseTree, argNode: Node): Function1Node {
            val name = nameNode.text
            val offset = (nameNode as TerminalNode).symbol.startIndex
            val function = Function1.findByName(name) ?: throw ParseException("Function not found: $name", offset)
            return Function1Node(function, argNode)
        }
    }

    override fun attributesForToString(): Map<String, Any> = mapOf("function" to function)

    override fun childrenForToString(): List<Node> = listOf(arg)
}
