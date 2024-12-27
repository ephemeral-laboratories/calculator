package garden.ephemeral.calculator.creals.util

import garden.ephemeral.calculator.creals.Real
import org.gciatto.kt.math.BigInteger

/**
 * Multiply by `2**n`, rounding result
 */
fun BigInteger.scale(n: Int): BigInteger {
    if (n >= 0) {
        return shl(n)
    } else {
        val adjK = shl(n + 1) + Real.BIG1
        return adjK.shr(1)
    }
}
