package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.values.Value
import garden.ephemeral.calculator.text.ValueFormat

/**
 * Base interface implemented by all expression nodes.
 */
interface Node {

    /**
     * Pretty prints the node.
     *
     * @param valueFormat the format to use for printing numbers.
     * @return the node as a pretty-printed string.
     */
    fun prettyPrint(valueFormat: ValueFormat): String

    /**
     * Evaluates the node, returning the final value.
     *
     * @return the evaluated value.
     */
    fun evaluate(): Value

    /**
     * Tests whether this node is close to another node with some
     * permitted error delta.
     *
     * @param other the other node.
     * @param delta the permitted error delta.
     * @return `true` if the node is close to the other node, `false` otherwise.
     */
    fun isCloseTo(other: Node, delta: Double): Boolean
}
