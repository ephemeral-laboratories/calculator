package garden.ephemeral.calculator.creals

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun closeTo(expectedString: String) = object : Matcher<Real> {
    override fun test(value: Real): MatcherResult {
        val actualString = value.toString(pointsOfPrecision = 20, radix = 10)

        return MatcherResult(
            passed = actualString == expectedString,
            failureMessageFn = { "Expected $value to be close to $expectedString but it's not." },
            negatedFailureMessageFn = { "$value is not close to $expectedString" },
        )
    }
}

infix fun Real.shouldBeCloseTo(expectedString: String) = this shouldBe closeTo(expectedString)
