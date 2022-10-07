package garden.ephemeral.calculator.nodes

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show

private const val EPSILON = 0.0000000000001

fun Assert<Node>.isCloseTo(expected: Node) = given { actual ->
    if (actual.isCloseTo(expected, EPSILON)) return
    expected("to be close to $expected but was:${show(actual)}")
}
