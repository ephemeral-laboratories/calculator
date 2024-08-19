package garden.ephemeral.calculator.creals.math

// Convenience overloads for BigInteger operators would potentially go here, if we wanted them.
// I don't see any particular need to have them directly in the class.

//operator fun BigInteger.plus(other: Int) = this + BigInteger.of(other)
//operator fun BigInteger.minus(other: Int) = this - BigInteger.of(other)
//operator fun BigInteger.times(other: Int) = this * BigInteger.of(other)
//operator fun BigInteger.div(other: Int) = this / BigInteger.of(other)
//operator fun BigInteger.rem(other: Int) = this % BigInteger.of(other)
//
//operator fun BigInteger.plus(other: Long) = this + BigInteger.of(other)
//operator fun BigInteger.minus(other: Long) = this - BigInteger.of(other)
//operator fun BigInteger.times(other: Long) = this * BigInteger.of(other)
//operator fun BigInteger.div(other: Long) = this / BigInteger.of(other)
//operator fun BigInteger.rem(other: Long) = this % BigInteger.of(other)

/**
 * Multiplies by `2**n`, rounding result.
 *
 * @param n the scale to apply.
 * @return a new [BigInteger] with that scale.
 */
fun BigInteger.scale(n: Int): BigInteger = when {
    n == 0 -> this
    n > 0 -> shl(n)
    else -> {
        val adjK = shl(n + 1) + BigInteger.ONE
        adjK.shr(1)
    }
}
