package garden.ephemeral.calculator.creals.math

/**
 * Arbitrarily big integer.
 *
 * @property digits the "digits" of the number. Stored in raw binary, essentially base 2**32.
 *           Most significant digits first.
 * @property sign the sign of the number.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class BigInteger private constructor(private val sign: Sign, private val digits: UIntArray) : Comparable<BigInteger> {
    init {
        if (digits.isNotEmpty()) {
            require(digits[0] != 0U) { "digits array contains leading zeroes: ${digits.contentToString()}" }
        }
    }

    // Arithmetic operators

    operator fun plus(other: BigInteger): BigInteger {
        return if (sign == other.sign) {
            BigInteger(
                sign = sign,
                digits = BigIntegerArrayHelpers.add(digits, other.digits),
            )
        } else {
            return minus(other.unaryMinus())
        }
    }

    operator fun minus(other: BigInteger): BigInteger {
        return if (sign == other.sign) {
            val digitComparison = BigIntegerArrayHelpers.compareDigits(digits, other.digits)
            when {
                (digitComparison > 0) -> BigInteger(
                    sign = sign,
                    digits = BigIntegerArrayHelpers.subtract(digits, other.digits),
                )
                (digitComparison < 0) -> BigInteger(
                    sign = sign.flip(),
                    digits = BigIntegerArrayHelpers.subtract(other.digits, digits),
                )
                else -> ZERO
            }
        } else {
            return plus(other.unaryMinus())
        }
    }

    operator fun times(other: BigInteger): BigInteger {
        return BigInteger(
            sign = if (sign == other.sign) Sign.POSITIVE else Sign.NEGATIVE,
            digits = BigIntegerArrayHelpers.multiply(digits, other.digits),
        )
    }

    operator fun div(other: BigInteger): BigInteger {
        return BigInteger(
            sign = if (sign == other.sign) Sign.POSITIVE else Sign.NEGATIVE,
            digits = BigIntegerArrayHelpers.divide(digits, other.digits).first,
        )
    }

    operator fun rem(other: BigInteger): BigInteger {
        return BigInteger(
            sign = sign,
            digits = BigIntegerArrayHelpers.divide(digits, other.digits).second,
        )
    }

    // Unary operators

    operator fun unaryPlus() = this

    operator fun unaryMinus() = BigInteger(
        sign = sign.flip(),
        digits = digits,
    )

    operator fun inc() = this + ONE

    operator fun dec() = this - ONE

    // Shifts and boolean operations

    infix fun shl(n: Int): BigInteger = TODO()
    infix fun shr(n: Int): BigInteger = TODO()

    infix fun and(other: BigInteger): BigInteger = TODO()
    infix fun or(other: BigInteger): BigInteger = TODO()
    infix fun xor(other: BigInteger): BigInteger = TODO()
    infix fun andNot(other: BigInteger): BigInteger = TODO()
    fun not(): BigInteger = TODO()

    fun testBit(n: Int): Boolean = TODO()
    fun setBit(n: Int): BigInteger = TODO()
    fun clearBit(n: Int): BigInteger = TODO()
    fun flipBit(n: Int): BigInteger = TODO()
    fun getLowestSetBit(): Int = TODO()
    fun bitLength(): Int = TODO()
    fun bitCount(): Int = TODO()

    // Comparisons

    override operator fun compareTo(other: BigInteger) = when (this.sign) {
        Sign.POSITIVE -> when (other.sign) {
            Sign.POSITIVE -> BigIntegerArrayHelpers.compareDigits(digits, other.digits)
            Sign.NEGATIVE -> 1
        }
        Sign.NEGATIVE -> when (other.sign) {
            Sign.POSITIVE -> -1
            Sign.NEGATIVE -> BigIntegerArrayHelpers.compareDigits(other.digits, digits)
        }
    }

    override fun equals(other: Any?) =
        (other is BigInteger) && digits.contentEquals(other.digits) && sign == other.sign

    override fun hashCode() = digits.contentHashCode() * 31 + sign.hashCode()

    // Misc

    override fun toString() = "BigInteger[sign=$sign, digits=${digits.contentToString()}]"

    companion object {
        internal const val STORAGE_BASE_LOG2 = 32
        internal val STORAGE_BASE = 1UL shl STORAGE_BASE_LOG2

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

            // Simple approach of reusing the existing * and + operations to build up the value as we parse it.
            // The rest BigInteger's equivalent method has some smarts which make it run faster, by processing
            // multiple digits in a single pass.
            var accumulator = ZERO
            val bigRadix = of(radix)
            while (charIndex < value.length) {
                val ch = value[charIndex]
                if (ch != '_') {
                    val digit = Character.digit(ch, radix)
                    accumulator = accumulator * bigRadix + of(digit)
                }
                charIndex++
            }

            return BigInteger(sign = sign, digits = accumulator.digits)
        }

        fun of(value: Int): BigInteger {
            val sign = Sign.ofInt(value)
            val digits = if (value == 0) {
                uintArrayOf()
            } else {
                when (sign) {
                    Sign.NEGATIVE -> uintArrayOf((-value).toUInt())
                    Sign.POSITIVE -> uintArrayOf(value.toUInt())
                }
            }
            return BigInteger(sign = sign, digits = digits)
        }

        fun of(value: Long): BigInteger {
            val sign = Sign.ofLong(value)
            val absValue = when (sign) {
                Sign.NEGATIVE -> (-value).toULong()
                Sign.POSITIVE -> value.toULong()
            }
            val digits = if (value == 0L) {
                uintArrayOf()
            } else {
                val highWord = (absValue shr 32).toUInt()
                val lowWord = absValue.toUInt()
                when {
                    highWord == 0U -> uintArrayOf(lowWord)
                    else -> uintArrayOf(highWord, lowWord)
                }
            }
            return BigInteger(sign = sign, digits = digits)
        }

        val ZERO = of(0)
        val ONE = of(1)
        val TEN = of(10)
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
}
