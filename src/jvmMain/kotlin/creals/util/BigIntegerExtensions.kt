package garden.ephemeral.calculator.creals.util

import garden.ephemeral.calculator.creals.Real
import java.math.BigInteger

/**
 * Multiply by `2**n`, rounding result
 */
fun BigInteger.scale(n: Int): BigInteger {
    if (n >= 0) {
        return shiftLeft(n)
    } else {
        val adjK = shiftLeft(n + 1) + Real.BIG1
        return adjK.shiftRight(1)
    }
}
