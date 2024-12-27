package garden.ephemeral.calculator.values

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

private const val EPSILON = 0.0000000000001

fun closeTo(expected: Value) = object : Matcher<Value> {
    override fun test(value: Value): MatcherResult {
        return MatcherResult(
            passed = value.isCloseTo(expected, EPSILON),
            failureMessageFn = { "Expected $value to be close to $expected but it's not." },
            negatedFailureMessageFn = { "$value is not close to $expected" },
        )
    }
}

infix fun Value.shouldBeCloseTo(expected: Value) = this shouldBe closeTo(expected)
