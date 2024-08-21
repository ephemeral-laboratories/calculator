package garden.ephemeral.calculator.creals.math

import kotlin.math.abs

/**
 * Arbitrarily big integer.
 *
 * @property sign the sign of the number.
 * @property digits the "digits" of the number. Stored in raw binary, essentially base 2**32.
 *           Most significant digits first.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class BigInteger private constructor(private val sign: Sign, private val magnitude: UnsignedBigInteger) : Comparable<BigInteger> {
    // Arithmetic operators

    operator fun plus(other: BigInteger): BigInteger {
        return if (sign == other.sign) {
            BigInteger(sign = sign, magnitude = magnitude + other.magnitude)
        } else {
            return minus(-other)
        }
    }

    operator fun minus(other: BigInteger): BigInteger {
        return if (sign == other.sign) {
            val comparison = magnitude.compareTo(other.magnitude)
            when {
                (comparison > 0) -> BigInteger(sign = sign, magnitude = magnitude - other.magnitude)
                (comparison < 0) -> BigInteger(sign = sign.flip(), magnitude = other.magnitude - magnitude)
                else -> ZERO
            }
        } else {
            return plus(-other)
        }
    }

    operator fun times(other: BigInteger): BigInteger {
        return BigInteger(
            sign = if (sign == other.sign) Sign.POSITIVE else Sign.NEGATIVE,
            magnitude = magnitude * other.magnitude,
        )
    }

    operator fun div(other: BigInteger): BigInteger {
        return BigInteger(
            sign = if (sign == other.sign) Sign.POSITIVE else Sign.NEGATIVE,
            magnitude = magnitude / other.magnitude,
        )
    }

    operator fun rem(other: BigInteger): BigInteger {
        return BigInteger(
            sign = sign,
            magnitude = magnitude % other.magnitude,
        )
    }

    fun divRem(other: BigInteger): QuotientWithRemainder {
        val magnitudeResult = magnitude.divRem(other.magnitude)
        return QuotientWithRemainder(
            BigInteger(
                sign = if (sign == other.sign) Sign.POSITIVE else Sign.NEGATIVE,
                magnitude = magnitudeResult.quotient,
            ),
            BigInteger(
                sign = sign,
                magnitude = magnitudeResult.remainder,
            ),
        )
    }

    // Unary operators

    operator fun unaryPlus() = this

    operator fun unaryMinus() = BigInteger(sign = sign.flip(), magnitude = magnitude)

    operator fun inc() = this + ONE

    operator fun dec() = this - ONE

    // Shifts and boolean operations

    /**
     * Shifts left the given number of bits.
     *
     * This parameter is considered signed - if a negative value is provided, it will shift right instead.
     *
     * @param n the number of bits to shift left.
     * @return the result.
     */
    infix fun shl(n: Int): BigInteger = when {
        n == 0 -> this
        n < 0 -> shr(-n)
        else -> BigInteger(sign = sign, magnitude = magnitude shl n)
    }

    /**
     * Shifts right the given number of bits.
     *
     * This parameter is considered signed - if a negative value is provided, it will shift left instead.
     *
     * @param n the number of bits to shift right.
     * @return the result.
     */
    infix fun shr(n: Int): BigInteger = when {
        n == 0 -> this
        n < 0 -> shl(-n)
        else -> BigInteger(sign = sign, magnitude = magnitude shr n)
    }

    /**
     * Multiplies by `2**n`, rounding result.
     *
     * @param n the scale to apply.
     * @return a new [BigInteger] with that scale.
     */
    fun scale(n: Int): BigInteger = when {
        n == 0 -> this
        n > 0 -> shl(n)
        else -> {
            val adjK = shl(n + 1) + ONE
            adjK.shr(1)
        }
    }

    // TODO: Are these methods even sensible when talking about signed values?
    // infix fun and(other: BigInteger): BigInteger = TODO()
    // infix fun or(other: BigInteger): BigInteger = TODO()
    // infix fun xor(other: BigInteger): BigInteger = TODO()
    // infix fun andNot(other: BigInteger): BigInteger = TODO()
    // fun not(): BigInteger = TODO()

    // fun testBit(n: Int): Boolean = TODO()
    // fun setBit(n: Int): BigInteger = TODO()
    // fun clearBit(n: Int): BigInteger = TODO()
    // fun flipBit(n: Int): BigInteger = TODO()
    // fun getLowestSetBit(): Int = TODO()
    // fun bitLength(): Int = TODO()
    // fun bitCount(): Int = TODO()

    // Comparisons

    override operator fun compareTo(other: BigInteger) = when (this.sign) {
        Sign.POSITIVE -> when (other.sign) {
            Sign.POSITIVE -> magnitude.compareTo(other.magnitude)
            Sign.NEGATIVE -> 1
        }
        Sign.NEGATIVE -> when (other.sign) {
            Sign.POSITIVE -> -1
            Sign.NEGATIVE -> other.magnitude.compareTo(magnitude)
        }
    }

    override fun equals(other: Any?) =
        (other is BigInteger) && sign == other.sign && magnitude == other.magnitude

    override fun hashCode() = sign.hashCode() * 31 + magnitude.hashCode()

    // Misc

    override fun toString() = "BigInteger[sign=$sign, words=$magnitude]"

    companion object {
        fun of(value: String, radix: Int = 10): BigInteger {
            require(value.isNotEmpty()) { "value must not be empty" }
            require(value.matches(Regex("^[+-]?[0-9_]+$"))) { "value must be a number" }

            var charIndex = 0
            var sign = Sign.POSITIVE
            when {
                value.startsWith("-") -> {
                    sign = Sign.NEGATIVE
                    charIndex++
                }
                value.startsWith("+") -> {
                    charIndex++
                }
            }

            return BigInteger(
                sign = sign,
                magnitude = UnsignedBigInteger.of(value.substring(charIndex), radix),
            )
        }

        fun of(value: Int): BigInteger {
            return BigInteger(sign = Sign.ofInt(value), magnitude = UnsignedBigInteger.of(abs(value).toUInt()))
        }

        fun of(value: Long): BigInteger {
            return BigInteger(sign = Sign.ofLong(value), magnitude = UnsignedBigInteger.of(abs(value).toULong()))
        }

        val ZERO = of(0)
        val ONE = of(1)
    }

    /**
     * Enumeration of signs for a number.
     * Usually you would also see a zero sign for the 0 case - this class treats zero
     * as positive.
     */
    enum class Sign {
        POSITIVE,
        NEGATIVE,
        ;

        fun flip() = if (this == POSITIVE) NEGATIVE else POSITIVE

        companion object {
            fun ofInt(value: Int) = if (value < 0) NEGATIVE else POSITIVE
            fun ofLong(value: Long) = if (value < 0) NEGATIVE else POSITIVE
        }
    }

    data class QuotientWithRemainder(
        val quotient: BigInteger,
        val remainder: BigInteger,
    )
}
