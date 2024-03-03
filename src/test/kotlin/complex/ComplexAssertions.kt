package garden.ephemeral.calculator.complex

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show

/**
 * Custom assertion to compare complex values approximately.
 *
 * @param expected the expected value.
 */
fun Assert<Complex>.isCloseTo(expected: Complex) = given { actual ->
    val actualString = actual.toString(pointsOfPrecision = 20, radix = 10)
    val expectedString = expected.toString(pointsOfPrecision = 20, radix = 10)
    if (actualString == expectedString) return
    expected("to be ${show(expected)} but was:${show(actual)}", expectedString, actualString)
}
