package garden.ephemeral.calculator.creals.util

import java.math.BigInteger

/**
 * Multiplies by `2**n`, rounding result.
 *
 * @param n the scale to apply.
 * @return a new [BigInteger] with that scale.
 */
fun BigInteger.scale(n: Int): BigInteger = when {
    n == 0 -> this
    n > 0 -> shiftLeft(n)
    else -> {
        val adjK = shiftLeft(n + 1) + BigInteger.ONE
        adjK.shiftRight(1)
    }
}
