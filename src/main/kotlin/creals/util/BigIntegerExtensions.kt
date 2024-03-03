package garden.ephemeral.calculator.creals.util

import java.math.BigInteger

// I thought Kotlin had convenience for these, but nope, I guess not.

operator fun BigInteger.plus(other: BigInteger): BigInteger = this.add(other)
operator fun BigInteger.minus(other: BigInteger): BigInteger = this.subtract(other)
operator fun BigInteger.times(other: BigInteger): BigInteger = this.multiply(other)
operator fun BigInteger.div(other: BigInteger): BigInteger = this.divide(other)
operator fun BigInteger.rem(other: BigInteger): BigInteger = this.remainder(other)
