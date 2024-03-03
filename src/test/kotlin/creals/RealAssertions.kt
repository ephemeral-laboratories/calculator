package garden.ephemeral.calculator.creals

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show

/**
 * Convenience method to assert a real value to a given number of decimal places.
 *
 * @param expected the expected value, as a string.
 */
fun Assert<Real>.isCloseTo(expected: String) = given { actual ->
    val result = actual.toString(pointsOfPrecision = 20, radix = 10)
    if (result == expected) return
    expected("to be ${show(expected)} but was:${show(result)}", expected, result)
}
