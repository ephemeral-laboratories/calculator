package garden.ephemeral.calculator

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show
import garden.ephemeral.calculator.nodes.Node

private const val EPSILON = 0.0000000000001

fun Assert<Node>.isCloseTo(expected: Node) = given { actual ->
    if (actual.isCloseTo(expected, EPSILON)) return
    expected("to be close to $expected but was:${show(actual)}")
}
