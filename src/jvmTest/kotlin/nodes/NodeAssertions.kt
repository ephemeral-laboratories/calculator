package garden.ephemeral.calculator.nodes

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

private const val EPSILON = 0.0000000000001

fun closeTo(expected: Node) = object : Matcher<Node> {
    override fun test(value: Node): MatcherResult {
        return MatcherResult(
            passed = value.isCloseTo(expected, EPSILON),
            failureMessageFn = { "Expected $value to be close to $expected but it's not." },
            negatedFailureMessageFn = { "$value is not close to $expected" },
        )
    }
}

infix fun Node.shouldBeCloseTo(expected: Node) = this shouldBe closeTo(expected)
