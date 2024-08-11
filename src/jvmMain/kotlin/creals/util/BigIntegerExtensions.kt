package garden.ephemeral.calculator.creals.util

import garden.ephemeral.calculator.creals.Real
import java.math.BigInteger

// I thought Kotlin had convenience for these, but nope, I guess not.

operator fun BigInteger.plus(other: BigInteger): BigInteger = this.add(other)
operator fun BigInteger.minus(other: BigInteger): BigInteger = this.subtract(other)
operator fun BigInteger.times(other: BigInteger): BigInteger = this.multiply(other)
operator fun BigInteger.div(other: BigInteger): BigInteger = this.divide(other)
operator fun BigInteger.rem(other: BigInteger): BigInteger = this.remainder(other)

/**
 * Multiply `this` by `2**n`.
 */
fun BigInteger.shift(n: Int): BigInteger {
    return when {
        n == 0 -> this
        n < 0 -> shiftRight(-n)
        else -> shiftLeft(n)
    }
}

/**
 * Multiply by `2**n`, rounding result
 */
fun BigInteger.scale(n: Int): BigInteger {
    if (n >= 0) {
        return shiftLeft(n)
    } else {
        val adjK = shift(n + 1) + Real.BIG1
        return adjK.shiftRight(1)
    }
}
