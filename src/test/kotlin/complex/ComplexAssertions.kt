package garden.ephemeral.calculator.complex

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun closeTo(expected: Complex) = object : Matcher<Complex> {
    override fun test(value: Complex): MatcherResult {
        val actualString = value.toString(pointsOfPrecision = 20, radix = 10)
        val expectedString = expected.toString(pointsOfPrecision = 20, radix = 10)

        return MatcherResult(
            passed = actualString == expectedString,
            failureMessageFn = { "Expected $value to be close to $expected but it's not." },
            negatedFailureMessageFn = { "$value is not close to $expected" },
        )
    }
}

infix fun Complex.shouldBeCloseTo(expected: Complex) = this shouldBe closeTo(expected)
