package garden.ephemeral.calculator.creals.math

import kotlin.concurrent.Volatile
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * Arbitrarily big integer.
 *
 * @property sign the sign of the number.
 * @property words multiple words making up the number. Most significant words come first.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class BigInteger(private val sign: Sign, private val words: UIntArray) : Comparable<BigInteger> {
    init {
        if (sign == Sign.Zero) {
            require(words.isEmpty()) { "words array is not empty for zero case" }
        } else {
            require(words.isNotEmpty()) { "words array is empty for non-zero case" }
            require(words[0] != 0U) { "words array contains leading zeroes: ${words.contentToString()}" }
        }
        require(words.size <= MAX_WORD_ARRAY_SIZE) { "words array is too large" }
    }

    /**
     * Returns the signum function for this [BigInteger].
     * This is a little awkward because we have chosen not to represent 0 as a sign value.
     *
     * @return `-1`, `0` or `1` if this value is negative, zero or positive.
     */
    fun signum() = sign.value

    /**
     * Gets the absolute value of this [BigInteger].
     *
     * @return the absolute value.
     */
    fun abs(): BigInteger = when {
        sign == Sign.Negative -> -this
        else -> this
    }

    // Arithmetic operators

    /**
     * Adds another [BigInteger] to this one.
     *
     * @param other the other value.
     * @return the sum of the two values.
     */
    operator fun plus(other: BigInteger): BigInteger {
        return if (sign == Sign.Zero) {
            // 0 + b simplifies to b
            other
        } else if (other.sign == Sign.Zero) {
            // a + 0 simplifies to a
            this
        } else if (sign == other.sign) {
            BigInteger(sign = sign, words = addWordArrays(words, other.words))
        } else {
            // a + (-b) simplifies to a - b
            minus(-other)
        }
    }

    /**
     * Subtracts another [BigInteger] from this one.
     *
     * @param other the other value.
     * @return the difference of the two values.
     */
    operator fun minus(other: BigInteger): BigInteger {
        return if (sign == Sign.Zero) {
            // 0 - b simplifies to -b
            -other
        } else if (other.sign == Sign.Zero) {
            // a - 0 simplifies to a
            this
        } else if (sign == other.sign) {
            val absComparison = compareWordArrays(words, other.words)
            when {
                (absComparison > 0) -> BigInteger(sign = sign, words = subtractWordArrays(words, other.words))
                (absComparison < 0) -> BigInteger(sign = sign.flip(), words = subtractWordArrays(other.words, words))
                else -> ZERO
            }
        } else {
            // a - (-b) simplifies to a + b
            plus(-other)
        }
    }

    /**
     * Multiplies this [BigInteger] with another one.
     *
     * @param other the other value.
     * @return the product of the two values.
     */
    operator fun times(other: BigInteger): BigInteger {
        if (sign == Sign.Zero || other.sign == Sign.Zero) {
            // a * 0 and 0 * b both simplify to 0
            return ZERO
        }

        return BigInteger(
            sign = if (sign == other.sign) Sign.Positive else Sign.Negative,
            words = multiplyWordArrays(words, other.words),
        )
    }

    /**
     * Divides this [BigInteger] by another one.
     *
     * Note that it is more performant to call [divRem] as computing the quotient
     * and remainder at the same time is essentially no more expensive than computing one or
     * the other.
     *
     * @param other the other value.
     * @return the quotient.
     */
    operator fun div(other: BigInteger) = divRem(other).quotient

    /**
     * Divides this [BigInteger] by another one, keeping the remainder.
     *
     * Note that it is more performant to call [divRem] as computing the quotient
     * and remainder at the same time is essentially no more expensive than computing one or
     * the other.
     *
     * @param other the other value.
     * @return the remainder.
     */
    operator fun rem(other: BigInteger) = divRem(other).remainder

    /**
     * Divides this [BigInteger] by another one, computing the quotient
     * and the remainder at the same time.
     *
     * @param other the other value.
     * @return the quotient and remainder.
     */
    fun divRem(other: BigInteger): QuotientWithRemainder {
        if (other.sign == Sign.Zero) {
            throw ArithmeticException("Division by zero")
        } else if (sign == Sign.Zero) {
            // 0 / b simplifies to 0
            return QuotientWithRemainder(ZERO, ZERO)
        }

        val dividendWords = words
        val divisorWords = other.words

        val quotientSign = if (sign == other.sign) Sign.Positive else Sign.Negative

        val absComparison = compareWordArrays(dividendWords, divisorWords)
        if (absComparison < 0) {
            // dividend is smaller than the divisor.
            return QuotientWithRemainder(quotient = ZERO, remainder = this)
        } else if (absComparison == 0) {
            // dividend is the same as the divisor.
            return QuotientWithRemainder(
                quotient = BigInteger(sign = quotientSign, words = uintArrayOf(1U)),
                remainder = ZERO,
            )
        }

        // Classical long division in binary
        val quotientWords = UIntArray(dividendWords.size)
        var remainderWords = uintArrayOf()

        for (wordIndex in dividendWords.indices) {
            var startIndexWithinWord = 31
            if (wordIndex == 0) {
                startIndexWithinWord -= dividendWords[0].countLeadingZeroBits()
            }
            var quotientWord = 0U
            for (indexWithinWord in startIndexWithinWord downTo 0) {
                // Append the current bit of the dividend to the remainder
                val currentBitOfDividend = (dividendWords[wordIndex] shr indexWithinWord) and 1U
                remainderWords = shiftWordArrayLeft(remainderWords, 1)
                if (remainderWords.isEmpty()) {
                    remainderWords = uintArrayOf(0U)
                }
                remainderWords[remainderWords.lastIndex] = remainderWords[remainderWords.lastIndex] or currentBitOfDividend

                // Can we subtract the divisor yet?
                if (compareWordArrays(remainderWords, divisorWords) >= 0) {
                    remainderWords = subtractWordArrays(remainderWords, divisorWords)
                    quotientWord = quotientWord or (1U shl indexWithinWord)
                }
            }
            quotientWords[wordIndex] = quotientWord
        }
        remainderWords = trimLeadingZeroes(remainderWords)

        val quotient = BigInteger(sign = quotientSign, words = trimLeadingZeroes(quotientWords))
        val remainder = if (remainderWords.isEmpty()) ZERO else BigInteger(sign = sign, words = remainderWords)

        return QuotientWithRemainder(quotient = quotient, remainder = remainder)
    }

    // Unary operators

    /**
     * Returns this value.
     *
     * @return this value.
     */
    operator fun unaryPlus() = this

    /**
     * Computes the additive inverse of this value.
     *
     * @return the negative of this value.
     */
    operator fun unaryMinus() = BigInteger(sign = sign.flip(), words = words)

    /**
     * Computes the increment of this value.
     *
     * @return `this + 1`
     */
    operator fun inc() = this + ONE

    /**
     * Computes the decrement of this value.
     *
     * @return `this - 1`
     */
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
        else -> BigInteger(sign = sign, words = shiftWordArrayLeft(words, n))
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
        else -> {
            val newWords = shiftWordArrayRight(words, n)
            if (newWords.isEmpty()) {
                ZERO
            } else {
                BigInteger(sign = sign, words = shiftWordArrayRight(words, n))
            }
        }
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

    /**
     * Performs a bitwise AND operation between this integer and another.
     * Treats both as two's complement.
     *
     * @param other the other integer.
     * @return the result.
     */
    infix fun and(other: BigInteger) = if (sign == Sign.Zero || other.sign == Sign.Zero) {
        ZERO
    } else {
        IntArrayHelpers.bitwiseBinaryOp(this, other) { a, b -> a and b }
    }

    /**
     * Performs a bitwise OR operation between this integer and another.
     * Treats both as two's complement.
     *
     * @param other the other integer.
     * @return the result.
     */
    infix fun or(other: BigInteger) = if (sign == Sign.Zero) {
        other
    } else if (other.sign == Sign.Zero) {
        this
    } else {
        IntArrayHelpers.bitwiseBinaryOp(this, other) { a, b -> a or b }
    }

    /**
     * Performs a bitwise XOR operation between this integer and another.
     * Treats both as two's complement.
     *
     * @param other the other integer.
     * @return the result.
     */
    infix fun xor(other: BigInteger) = if (sign == Sign.Zero) {
        other
    } else if (other.sign == Sign.Zero) {
        this
    } else {
        IntArrayHelpers.bitwiseBinaryOp(this, other) { a, b -> a xor b }
    }

    /**
     * Performs a bitwise AND NOT operation between this integer and another.
     * Treats both as two's complement.
     *
     * Equivalent to `and(other.not())`, but should produce less temporary heap data.
     *
     * @param other the other integer.
     * @return the result.
     */
    infix fun andNot(other: BigInteger) = if (sign == Sign.Zero) {
        ZERO
    } else if (other.sign == Sign.Zero) {
        this
    } else {
        IntArrayHelpers.bitwiseBinaryOp(this, other) { a, b -> a and b.inv() }
    }

    /**
     * Performs a bitwise NOT operation on this integer.
     * Treats the integer as two's complement.
     *
     * @return the result.
     */
    fun not() = IntArrayHelpers.bitwiseUnaryOp(this) { a -> a.inv() }

    /**
     * Returns `true` if and only if the designated bit is set.
     * (Computes `((this & (1<<n)) != 0)`.)
     *
     * @param n the index of the bit to test.
     * @return `true` if and only if the designated bit is set.
     * @throws ArithmeticException if `n` is negative.
     */
    fun testBit(n: Int): Boolean {
        if (n < 0) {
            throw ArithmeticException("Negative bit address")
        }

        return (IntArrayHelpers.getInt(this, n ushr 5) and (1 shl (n and 31))) != 0
    }

    /**
     * Creates a copy of this integer with a bit set.
     *
     * @param n the index of the bit to set.
     * @return the new integer.
     * @throws ArithmeticException if `n` is negative.
     */
    fun setBit(n: Int): BigInteger {
        if (n < 0) {
            throw ArithmeticException("Negative bit address")
        }

        return IntArrayHelpers.manipulateBit(this, n) { a, b -> a or b }
    }

    /**
     * Creates a copy of this integer with a bit cleared.
     *
     * @param n the index of the bit to clear.
     * @return the new integer.
     * @throws ArithmeticException if `n` is negative.
     */
    fun clearBit(n: Int): BigInteger {
        if (n < 0) {
            throw ArithmeticException("Negative bit address")
        }

        return IntArrayHelpers.manipulateBit(this, n) { a, b -> a and b.inv() }
    }

    /**
     * Creates a copy of this integer with a bit flipped.
     *
     * @param n the index of the bit to flip.
     * @return the new integer.
     * @throws ArithmeticException if `n` is negative.
     */
    fun flipBit(n: Int): BigInteger {
        if (n < 0) {
            throw ArithmeticException("Negative bit address")
        }

        return IntArrayHelpers.manipulateBit(this, n) { a, b -> a xor b }
    }

    /**
     * The index of the rightmost (lowest-order) one bit in this [BigInteger]
     * (the number of zero bits to the right of the rightmost one bit).
     * Returns -1 if this [BigInteger] contains no one bits.
     *
     * (Computes `(this == 0? -1 : log2(this & -this))`.)
     *
     * @return the index of the rightmost one bit in this [BigInteger].
     */
    val lowestSetBit: Int by lazy {
        if (sign == Sign.Zero) {
            -1
        } else {
            var magTrailingZeroCount = 0
            var j: Int = words.lastIndex
            while (words[j] == 0U) {
                magTrailingZeroCount += 32
                j--
            }
            magTrailingZeroCount += words[j].countTrailingZeroBits()
            magTrailingZeroCount
        }
    }

    /**
     * Gets the number of bits in the two's complement representation of this [BigInteger]
     * that differ from its sign.
     * This method is useful when implementing bit-vector style sets.
     *
     * @return the number of bits in the two's complement representation of this [BigInteger]
     *         that differ from its sign.
     */
    val bitCount: Int by lazy {
        if (sign == Sign.Zero) {
            return@lazy 0
        }
        var bitCount = words.sumOf { word -> word.countOneBits() }
        if (sign == Sign.Negative) {
            bitCount += lowestSetBit - 1
        }
        bitCount
    }

    /**
     * Gets the number of bits in the minimal two's-complement representation of this [BigInteger].
     * Excludes the sign bit.
     *
     * For positive values, this is equivalent to the number of bits in the ordinary binary representation.
     * For zero, this method returns `0`.
     *
     * (Computes ` (ceil(log2(this < 0 ? -this : this + 1)))`.)
     *
     * @return number of bits in the minimal two's-complement representation of this [BigInteger], _excluding_ sign.
     */
    val bitLength: Int by lazy {
        if (sign == Sign.Zero) {
            return@lazy 0
        }
        var bitLength = STORAGE_BASE_LOG2 * words.size - words[0].countLeadingZeroBits()
        if (sign == Sign.Negative) {
            val absIsPowerOf2 = words[0].countOneBits() == 1 && words.indexOfLast { x -> x != 0U } == 0
            if (absIsPowerOf2) {
                bitLength -= 1
            }
        }
        bitLength
    }

    // Comparisons

    /**
     * Compares with another [BigInteger] for ordering.
     *
     * Compares the signs first, with positive sorting last.
     * If signs are the same, compares the sizes of the word arrays next  - a longer array is always a larger value.
     * If the sizes are equal, compares each corresponding word, starting with the most
     * significant words, which are stored first.
     *
     * @param other the other value to compare against.
     * @return `1` if our value is greater,
     *         `-1` if the other value is greater,
     *         `0` if the two are equal.
     */
    override operator fun compareTo(other: BigInteger): Int {
        val signComparison = sign.compareTo(other.sign)
        if (signComparison != 0 || sign == Sign.Zero) {
            return signComparison
        }

        var wordsComparison = compareWordArrays(words, other.words)
        if (wordsComparison != 0) {
            // Sort small negative values above large negative values
            if (sign == Sign.Negative) {
                wordsComparison = -wordsComparison
            }
            return wordsComparison
        }

        return 0
    }

    override fun equals(other: Any?) =
        (other is BigInteger) && sign == other.sign && words.contentEquals(other.words)

    override fun hashCode() = sign.hashCode() * 31 + words.contentHashCode()

    // Narrowing conversions

    /**
     * The first index where [IntArrayHelpers.getInt] for that index returns non-zero.
     *
     * Cached here for performance. If I had a way to move it to `IntArrayHelpers` it would be there instead.
     */
    private val indexOfFirstNonzeroInt: Int by lazy {
        val wordCount = words.size
        var i = wordCount - 1
        while (i >= 0 && words[i] == 0U) {
            i--
        }
        wordCount - i - 1
    }

    /**
     * Converts this [BigInteger] to a [Long].
     *
     * If it is too big to fit in a `Long`, only the low-order 64 bits are returned.
     * Note that this conversion can lose information about the overall magnitude of
     * the value, as well as return a result with the opposite sign.
     *
     * @return this [BigInteger] converted to a [Long].
     * @see [toLongExact]
     */
    fun toLong(): Long {
        var result = 0L
        for (i in 1 downTo 0) {
            result = (result shl 32) + IntArrayHelpers.getInt(this, i).toUInt().toLong()
        }
        return result
    }

    /**
     * Converts this [BigInteger] to a [Long], checking for lost information.
     *
     * If it is out of the range of the `Long` type, then an [ArithmeticException] is thrown.
     *
     * @return this [BigInteger] converted to a [Long].
     * @throws ArithmeticException if the value cannot fit in a `Long`.
     * @see [toLong]
     */
    fun toLongExact() = toLongExactOrNull() ?: throw ArithmeticException("BigInteger out of long range")

    /**
     * Converts this [BigInteger] to a [Long], checking for lost information.
     *
     * If it is out of the range of the `Long` type, returns `null`.
     *
     * @return this [BigInteger] converted to a [Long], or `null`.
     * @see [toLong]
     */
    fun toLongExactOrNull() = if (words.size <= 2 && bitLength <= 63) toLong() else null

    /**
     * Converts this [BigInteger] to an [Int].
     *
     * If it is too big to fit in an `Int`, only the low-order 32 bits are returned.
     * Note that this conversion can lose information about the overall magnitude of
     * the value, as well as return a result with the opposite sign.
     *
     * @return this [BigInteger] converted to a [Int].
     * @see [toIntExact]
     */
    fun toInt() = IntArrayHelpers.getInt(this, 0)

    /**
     * Converts this [BigInteger] to an [Int], checking for lost information.
     *
     * If it is out of the range of the `Int` type, then an [ArithmeticException] is thrown.
     *
     * @return this [BigInteger] converted to an [Int].
     * @throws ArithmeticException if the value cannot fit in an `Int`.
     * @see [toInt]
     */
    fun toIntExact() = toIntExactOrNull() ?: throw ArithmeticException("BigInteger out of int range")

    /**
     * Converts this [BigInteger] to an [Int], checking for lost information.
     *
     * If it is out of the range of the `Int` type, returns `null`.
     *
     * @return this [BigInteger] converted to an [Int], or `null`.
     * @see [toInt]
     */
    fun toIntExactOrNull(): Int? = if (words.size <= 1 && bitLength <= 31) toInt() else null

    /**
     * Converts this [BigInteger] to a [Short].
     *
     * If it is too big to fit in a `Short`, only the low-order 16 bits are returned.
     * Note that this conversion can lose information about the overall magnitude of
     * the value, as well as return a result with the opposite sign.
     *
     * @return this [BigInteger] converted to a [Short].
     * @see [toShortExact]
     */
    fun toShort() = toInt().toShort()

    /**
     * Converts this [BigInteger] to a [Short], checking for lost information.
     *
     * If it is out of the range of the `Short` type, then an [ArithmeticException] is thrown.
     *
     * @return this [BigInteger] converted to a [Short].
     * @throws ArithmeticException if the value cannot fit in an `Short`.
     * @see [toShort]
     */
    fun toShortExact() = toShortExactOrNull() ?: throw ArithmeticException("BigInteger out of short range")

    /**
     * Converts this [BigInteger] to a [Short], checking for lost information.
     *
     * If it is out of the range of the `Short` type, returns `null`.
     *
     * @return this [BigInteger] converted to a [Short], or `null`.
     * @see [toShort]
     */
    fun toShortExactOrNull(): Short? = if (words.size <= 1 && bitLength <= 15) toShort() else null

    /**
     * Converts this [BigInteger] to a [Byte].
     *
     * If it is too big to fit in a `Byte`, only the low-order 8 bits are returned.
     * Note that this conversion can lose information about the overall magnitude of
     * the value, as well as return a result with the opposite sign.
     *
     * @return this [BigInteger] converted to a [Byte].
     * @see [toByteExact]
     */
    fun toByte(): Byte = toInt().toByte()

    /**
     * Converts this [BigInteger] to a [Byte], checking for lost information.
     *
     * If it is out of the range of the `Byte` type, then an [ArithmeticException] is thrown.
     *
     * @return this [BigInteger] converted to a [Byte].
     * @throws ArithmeticException if the value cannot fit in an `Byte`.
     * @see [toByte]
     */
    fun toByteExact(): Byte = toByteExactOrNull() ?: throw ArithmeticException("BigInteger out of byte range")

    /**
     * Converts this [BigInteger] to a [Byte], checking for lost information.
     *
     * If it is out of the range of the `Byte` type, returns `null`.
     *
     * @return this [BigInteger] converted to a [Byte], or `null`.
     * @see [toByte]
     */
    fun toByteExactOrNull(): Byte? = if (words.size <= 1 && bitLength <= 7) toByte() else null

    // Consider these. Do we detect exact for these too?
    fun toFloat(): Float = TODO()
    fun toDouble(): Double = TODO()

    // Misc

    override fun toString(): String {
        return toString(10)
    }

    /**
     * Converts to a string for a specific radix.
     *
     * @param radix the radix.
     * @return the string.
     */
    fun toString(radix: Int): String {
        require(radix in Character.MIN_RADIX..Character.MAX_RADIX)
        if (sign == Sign.Zero) {
            return "0"
        }

        val abs = abs()

        // Calculate the builder size in advance so that it doesn't have to grow its array dynamically.
        val numChars = (floor(abs.bitLength * PowerCache.LOG_TWO / PowerCache.getCachedLog(radix)) + 1).toInt() +
                (if (sign == Sign.Negative) 1 else 0)
        val builder = StringBuilder(numChars)

        if (sign == Sign.Negative) {
            builder.append('-')
        }

        ToStringHelpers.recursiveToString(abs, builder, radix, 0)

        return builder.toString()
    }

    companion object {
        fun of(value: String, radix: Int = 10): BigInteger {
            require(value.isNotEmpty()) { "value must not be empty" }
            if (radix <= 10) {
                require(value.matches(Regex("^[+-]?[0-9_]+$"))) { "value must be a number" }
            } else {
                require(value.matches(Regex("^[+-]?[0-9a-z_]+$"))) { "value must be a number" }
            }

            var charIndex = 0
            var sign = Sign.Positive
            when {
                value.startsWith("-") -> {
                    sign = Sign.Negative
                    charIndex++
                }

                value.startsWith("+") -> {
                    charIndex++
                }
            }

            // Simple approach of reusing the existing * and + operations to build up the value as we parse it.
            // The rest BigInteger's equivalent method has some smarts which make it run faster, by processing
            // multiple digits in a single pass.
            var resultWords = uintArrayOf()
            val radixWords = uintArrayOf(radix.toUInt())
            while (charIndex < value.length) {
                val ch = value[charIndex]
                if (ch != '_') {
                    val digit = Character.digit(ch, radix)
                    resultWords = multiplyWordArrays(resultWords, radixWords)
                    resultWords = addWordArrays(resultWords, uintArrayOf(digit.toUInt()))
                }
                charIndex++
            }

            return if (resultWords.isEmpty()) {
                ZERO
            } else {
                BigInteger(sign = sign, words = resultWords)
            }
        }

        fun of(value: Int): BigInteger {
            ConstantCache.getCached(value)?.let { return it }
            val words = when {
                value < 0 -> uintArrayOf((-value).toUInt())
                else -> uintArrayOf(value.toUInt())
            }
            return BigInteger(sign = Sign.ofInt(value), words = words)
        }

        fun of(value: UInt): BigInteger {
            ConstantCache.getCached(value)?.let { return it }
            val words = uintArrayOf(value)
            return BigInteger(sign = Sign.Positive, words = words)
        }

        fun of(value: Long): BigInteger {
            ConstantCache.getCached(value)?.let { return it }
            if (value == Long.MIN_VALUE) {
                // Negating Long.MIN_VALUE returns Long.MIN_VALUE
                return BigInteger(sign = Sign.Negative, words = uintArrayOf((value shr 32).toUInt(), value.toUInt()))
            }
            val absValue = if (value < 0L) -value else value
            val words = when {
                absValue <= UInt.MAX_VALUE.toLong() -> uintArrayOf(absValue.toUInt())
                else -> uintArrayOf((absValue shr 32).toUInt(), absValue.toUInt())
            }
            return BigInteger(sign = Sign.ofLong(value), words = trimLeadingZeroes(words))
        }

        fun of(value: ULong): BigInteger {
            ConstantCache.getCached(value)?.let { return it }
            val words = when {
                value <= UInt.MAX_VALUE.toULong() -> uintArrayOf(value.toUInt())
                else -> uintArrayOf((value shr 32).toUInt(), value.toUInt())
            }
            return BigInteger(sign = Sign.Positive, words = words)
        }

        val ZERO = ConstantCache.getCached(0)!!
        val ONE = ConstantCache.getCached(1)!!

        private const val STORAGE_BASE_LOG2 = 32
        private val STORAGE_BASE = 1UL shl STORAGE_BASE_LOG2
        private const val MAX_WORD_ARRAY_SIZE = Int.MAX_VALUE / Integer.SIZE + 1 // (1 << 26)

        private fun trimLeadingZeroes(words: UIntArray): UIntArray =
            when (val indexOfFirstNonZero = words.indexOfFirst { word -> word != 0U }) {
                0 -> words
                -1 -> uintArrayOf()
                else -> words.copyOfRange(fromIndex = indexOfFirstNonZero, toIndex = words.size)
            }

        private fun presentResultWords(accumulator: MutableList<UInt>): UIntArray {
            // Trim trailing zeroes. These would become leading zeroes once the list is reversed,
            // and we can't allow for the possibility of there being more than one way to represent
            // a single numeric value.
            while (accumulator.isNotEmpty() && accumulator[accumulator.lastIndex] == 0U) {
                accumulator.removeLast()
            }
            return accumulator.reversed().toUIntArray()
        }

        private fun addWordArrays(leftAddendWords: UIntArray, rightAddendWords: UIntArray): UIntArray {
            val result = mutableListOf<UInt>()
            var carry = 0UL
            var leftAddendIndex = leftAddendWords.lastIndex
            var rightAddendIndex = rightAddendWords.lastIndex

            while (leftAddendIndex >= 0 || rightAddendIndex >= 0 || carry > 0UL) {
                val leftAddendWord = if (leftAddendIndex >= 0) leftAddendWords[leftAddendIndex].toULong() else 0UL
                val rightAddendWord = if (rightAddendIndex >= 0) rightAddendWords[rightAddendIndex].toULong() else 0UL
                val sum = leftAddendWord + rightAddendWord + carry

                result += sum.toUInt()
                carry = sum shr STORAGE_BASE_LOG2

                leftAddendIndex--
                rightAddendIndex--
            }

            return presentResultWords(result)
        }

        private fun subtractWordArrays(minuendWords: UIntArray, subtrahendWords: UIntArray): UIntArray {
            val comparison = compareWordArrays(minuendWords, subtrahendWords)
            if (comparison == 0) {
                return uintArrayOf()
            } else if (comparison < 0) {
                throw ArithmeticException("other > this")
            }

            val result = mutableListOf<UInt>()
            var carry = 0UL
            var minuendIndex = minuendWords.lastIndex
            var subtrahendIndex = subtrahendWords.lastIndex

            while (minuendIndex >= 0 || subtrahendIndex >= 0 || carry > 0UL) {
                var minuendWord = if (minuendIndex >= 0) minuendWords[minuendIndex].toULong() else 0UL
                val subtrahendWord = if (subtrahendIndex >= 0) subtrahendWords[subtrahendIndex].toULong() else 0UL

                if (minuendWord < subtrahendWord) {
                    minuendWord += STORAGE_BASE
                }

                val difference = minuendWord - subtrahendWord - carry
                result += difference.toUInt()
                carry = if (minuendWord >= STORAGE_BASE) 1UL else 0UL

                minuendIndex--
                subtrahendIndex--
            }

            return presentResultWords(result)
        }

        private fun multiplyWordArrays(leftFactorWords: UIntArray, rightFactorWords: UIntArray): UIntArray {
            val results = mutableListOf<UIntArray>()
            val lastLeftIndex = leftFactorWords.lastIndex
            val lastRightIndex = rightFactorWords.lastIndex

            for (rightIndex in lastRightIndex downTo 0) {
                val currentResult = mutableListOf<UInt>()

                // Entries need progressively more zeroes as you go further down.
                // Similar to how you offset the numbers when writing out long multiplication.
                repeat(lastRightIndex - rightIndex) {
                    currentResult.add(0U)
                }

                var carry = 0UL
                val rightFactorWord = rightFactorWords[rightIndex].toULong()
                for (leftIndex in lastLeftIndex downTo 0) {
                    val leftFactorWord = leftFactorWords[leftIndex].toULong()
                    val sum = leftFactorWord * rightFactorWord + carry

                    currentResult.add(sum.toUInt())
                    carry = sum shr STORAGE_BASE_LOG2
                }
                if (carry > 0UL) {
                    currentResult.add(carry.toUInt())
                }
                results.add(presentResultWords(currentResult))
            }

            return results.fold(uintArrayOf()) { acc, words -> addWordArrays(acc, words) }
        }

        private fun shiftWordArrayLeft(words: UIntArray, n: Int): UIntArray {
            // Zero case
            if (words.isEmpty()) return uintArrayOf()

            require(n > 0)
            val nHigh = n / STORAGE_BASE_LOG2
            val nLow = n % STORAGE_BASE_LOG2

            // Check whether we need to grow the array.
            var newWords = if (words[0].countLeadingZeroBits() - nLow < 0) {
                UIntArray(words.size + 1).apply {
                    words.copyInto(this, destinationOffset = 1)
                }
            } else {
                words.copyOf()
            }

            if (newWords.size + nHigh > MAX_WORD_ARRAY_SIZE) {
                throw ArithmeticException("Array would exceed max possible size of $MAX_WORD_ARRAY_SIZE")
            }

            for (i in newWords.indices) {
                var word = newWords[i]

                // low bits from this word shift towards the high bits
                word = word.shl(nLow)

                if (i < newWords.lastIndex) {
                    // high bits from the next word go into the low bits for this one
                    word = word or (newWords[i + 1] shr (32 - nLow))
                }

                newWords[i] = word
            }

            if (nHigh > 0) {
                // Shift is large enough that entire new 0 words go on the end of the array.
                // This is done after the rest of the shifting so that we don't need to look at the zero elements.
                newWords = newWords + UIntArray(nHigh)
            }

            return trimLeadingZeroes(newWords)
        }

        private fun shiftWordArrayRight(words: UIntArray, n: Int): UIntArray {
            // Zero case
            if (words.isEmpty()) return uintArrayOf()

            require(n > 0)
            val nHigh = n / STORAGE_BASE_LOG2
            val nLow = n % STORAGE_BASE_LOG2

            val newWords = if (nHigh > 0) {
                // Shift is large enough that entire words disappear from the end of the array
                val newSize = words.size - nHigh
                if (newSize <= 0) {
                    // Edge case, shifting right sufficiently far will always result in zero
                    return uintArrayOf()
                }
                words.copyOf(newSize = newSize)
            } else {
                words.copyOf()
            }

            for (i in newWords.lastIndex downTo 0) {
                var word = newWords[i]

                // high bits from this word shift towards the low bits
                word = word.shr(nLow)

                if (i > 0) {
                    // low bits from the previous word go into the high bits for this one
                    word = word or (newWords[i - 1] shl (32 - nLow))
                }

                newWords[i] = word
            }

            return trimLeadingZeroes(newWords)
        }

        private fun compareWordArrays(first: UIntArray, second: UIntArray): Int {
            val sizeComparison = first.size.compareTo(second.size)
            if (sizeComparison != 0) {
                return sizeComparison
            }
            first.indices.forEach { index ->
                val wordComparison = first[index].compareTo(second[index])
                if (wordComparison != 0) {
                    return wordComparison
                }
            }
            return 0
        }
    }

    /**
     * Enumeration of sign values.
     *
     * @property value the corresponding sign value.
     */
    enum class Sign(val value: Int) {
        // Negative comes first so that compareTo works sensibly.
        Negative(-1),
        Zero(0),
        Positive(1),
        ;

        /**
         * Flips the sign.
         *
         * @return the negated sign.
         */
        fun flip() = when (this) {
            Negative -> Positive
            Zero -> Zero
            Positive -> Negative
        }

        companion object {
            /**
             * Gets the enum value which has its `value` set to the provided value.
             *
             * @param value the sign value.
             * @return the enum value.
             * @throws IllegalArgumentException if the value is not a valid sign.
             */
            fun ofValue(value: Int) = when (value) {
                -1 -> Negative
                0 -> Zero
                1 -> Positive
                else -> throw IllegalArgumentException("Invalid sign value: $value")
            }

            /**
             * Gets the enum value corresponding to the sign of the given `int` value.
             *
             * @param value the `int` value.
             * @return the sign.
             */
            fun ofInt(value: Int) = ofValue(value.sign)

            /**
             * Gets the enum value corresponding to the sign of the given `long` value.
             *
             * @param value the `long` value.
             * @return the sign.
             */
            fun ofLong(value: Long) = ofValue(value.sign)
        }
    }

    /**
     * Result class containing a quotient and remainder for a single division operation.
     */
    data class QuotientWithRemainder(
        val quotient: BigInteger,
        val remainder: BigInteger,
    )

    /**
     * Cache of a fixed number of small constant values, for performance.
     */
    internal object ConstantCache {
        private const val MAX_CONSTANT = 16
        private val positiveConstants: Array<BigInteger>
        private val negativeConstants: Array<BigInteger>

        init {
            // We share the constant arrays, but the word arrays inside each object are the same for
            // both the positive and the negative cases, so we share those as well.
            val sharedWordArrays = (0..MAX_CONSTANT)
                .map { n -> if (n == 0) uintArrayOf() else uintArrayOf(n.toUInt()) }
            positiveConstants = sharedWordArrays
                .map { words -> BigInteger(sign = if (words.isEmpty()) Sign.Zero else Sign.Positive, words = words) }
                .toTypedArray()
            negativeConstants = sharedWordArrays
                .map { words -> BigInteger(sign = if (words.isEmpty()) Sign.Zero else Sign.Negative, words = words) }
                .toTypedArray()
        }

        internal fun getCached(value: Int) = when (value) {
            in 0..MAX_CONSTANT -> positiveConstants[value]
            in -MAX_CONSTANT..0 -> negativeConstants[-value]
            else -> null
        }

        internal fun getCached(value: UInt) = when (value) {
            in 0U..MAX_CONSTANT.toUInt() -> positiveConstants[value.toInt()]
            else -> null
        }

        internal fun getCached(value: Long) = when (value) {
            in 0L..MAX_CONSTANT.toLong() -> positiveConstants[value.toInt()]
            in -MAX_CONSTANT..0 -> negativeConstants[-value.toInt()]
            else -> null
        }

        internal fun getCached(value: ULong) = when (value) {
            in 0UL..MAX_CONSTANT.toULong() -> positiveConstants[value.toInt()]
            else -> null
        }
    }

    /**
     * Cache of powers and logs for each radix.
     */
    internal object PowerCache {
        /**
         * The cache of powers of each radix.
         *
         * Initialised with just the first value. Additional values will be created on demand.
         */
        @Volatile
        private var powerCache = Array(Character.MAX_RADIX + 1) { i -> arrayOf(of(i.toLong())) }

        /**
         * The cache of logarithms of radices for base conversion.
         */
        private val logCache = DoubleArray(Character.MAX_RADIX + 1) { i -> ln(i.toDouble()) }

        /**
         * The natural log of 2.
         */
        internal val LOG_TWO: Double = ln(2.0)

        /**
         * Gets the value `radix^(2^exponent)` from the cache.
         * If this value doesn't already exist in the cache, it is added.
         */
        internal fun getCachedPower(radix: Int, exponent: Int): BigInteger {
            var cacheLine = powerCache[radix] // volatile read
            if (exponent < cacheLine.size) {
                return cacheLine[exponent]
            }

            // Copy to a list in order to add the new elements to the end.
            // The original Java code for this copied the array, but I found it hard to reconcile
            // that with Kotlin null safety.
            val temp = cacheLine.toMutableList()
            var tempValue = temp[temp.lastIndex]
            while (temp.size < exponent + 1) {
                // tempValue.pow(2)
                tempValue *= tempValue
                temp.add(tempValue)
            }
            cacheLine = temp.toTypedArray()

            // Someone else may have modified it in another thread while we were working on it,
            // so the code here checks again whether the exponent is cached.

            var powerCacheCopy = powerCache // volatile read again
            if (exponent >= powerCacheCopy[radix].size) {
                powerCacheCopy = powerCacheCopy.clone()
                powerCacheCopy[radix] = cacheLine
                powerCache = powerCacheCopy // volatile write, publish
            }

            return cacheLine[exponent]
        }

        /**
         * Gets a log value from the log cache..
         */
        internal fun getCachedLog(radix: Int) = logCache[radix]
    }

    /**
     * Helpers related to [BigInteger.toString].
     */
    internal object ToStringHelpers {
        private const val ZEROS_LENGTH = 63
        private val ZEROS = "0".repeat(ZEROS_LENGTH)

        /**
         * The number of digits of the given radix that can fit in a [Long] without "going negative".
         */
        private var digitsPerLong = intArrayOf(
            0, 0,
            62, 39, 31, 27, 24, 22, 20, 19, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14,
            14, 14, 14, 13, 13, 13, 13, 13, 13, 12, 12, 12, 12, 12, 12, 12, 12
        )

        /**
         * The threshold value for using Schoenhage recursive base conversion.
         */
        private const val SCHOENHAGE_BASE_CONVERSION_THRESHOLD = 20

        /**
         * The "long radix" that tears each number into "long digits", each of which consists of
         * the number of digits in the corresponding element in [digitsPerLong]
         * (`longRadix\[i] = i**digitPerLong\[i]`).
         */
        private var longRadix = arrayOf(
            ZERO, ZERO,
            of(0x4000000000000000L), of(0x383d9170b85ff80bL),
            of(0x4000000000000000L), of(0x6765c793fa10079dL),
            of(0x41c21cb8e1000000L), of(0x3642798750226111L),
            of(0x1000000000000000L), of(0x12bf307ae81ffd59L),
            of(0xde0b6b3a7640000L), of(0x4d28cb56c33fa539L),
            of(0x1eca170c00000000L), of(0x780c7372621bd74dL),
            of(0x1e39a5057d810000L), of(0x5b27ac993df97701L),
            of(0x1000000000000000L), of(0x27b95e997e21d9f1L),
            of(0x5da0e1e53c5c8000L), of(0xb16a458ef403f19L),
            of(0x16bcc41e90000000L), of(0x2d04b7fdd9c0ef49L),
            of(0x5658597bcaa24000L), of(0x6feb266931a75b7L),
            of(0xc29e98000000000L), of(0x14adf4b7320334b9L),
            of(0x226ed36478bfa000L), of(0x383d9170b85ff80bL),
            of(0x5a3c23e39c000000L), of(0x4e900abb53e6b71L),
            of(0x7600ec618141000L), of(0xaee5720ee830681L),
            of(0x1000000000000000L), of(0x172588ad4f5f0981L),
            of(0x211e44f7d02c1000L), of(0x2ee56725f06e5c71L),
            of(0x41c21cb8e1000000L)
        )

        /**
         * If `numZeros > 0`, appends that many zeros to the specified [StringBuilder].
         * Uses a string of zeroes to make this slightly faster than just appending
         * individual characters over and over.
         *
         * @param builder the [StringBuilder] to append to
         * @param numZeros the number of zeros to append.
         */
        private fun padWithZeros(builder: StringBuilder, numZeros: Int) {
            var temp = numZeros
            while (temp >= ZEROS_LENGTH) {
                builder.append(ZEROS)
                temp -= ZEROS_LENGTH
            }
            if (temp > 0) {
                builder.append(ZEROS, 0, temp)
            }
        }

        /**
         * Performs `toString` when arguments are small. The value must be non-negative!
         * Performs no padding if `digits <= 0`.
         *
         * @param value the value to format to a string.
         * @param builder the [StringBuilder] to append to.
         * @param radix the base to convert to.
         * @param digits the minimum number of digits to pad to.
         * @see [recursiveToString]
         */
        private fun smallToString(value: BigInteger, builder: StringBuilder, radix: Int, digits: Int) {
            assert(radix in Character.MIN_RADIX..Character.MAX_RADIX)
            assert(value.sign != Sign.Negative)

            if (value.sign == Sign.Zero) {
                return padWithZeros(builder, digits)
            }

            // Compute upper bound on number of digit groups and allocate space
            val maxNumDigitGroups = (4 * value.words.size + 6) / 7
            val digitGroups = LongArray(maxNumDigitGroups)

            // Translate number to string, a digit group at a time
            var temp = value
            var numGroups = 0
            while (temp.sign != Sign.Zero) {
                val (quotient, remainder) = temp.divRem(longRadix[radix])
                digitGroups[numGroups++] = remainder.toLong()
                temp = quotient
            }

            // Put first digit group into result buffer
            var digitsString = digitGroups[numGroups - 1].toString(radix)
            padWithZeros(builder, digits - (digitsString.length + (numGroups - 1) * digitsPerLong[radix]))
            builder.append(digitsString)

            // Append remaining digit groups each padded with leading zeros
            for (i in numGroups - 2 downTo 0) {
                digitsString = digitGroups[i].toString(radix)
                padWithZeros(builder, digitsPerLong[radix] - digitsString.length)
                builder.append(digitsString)
            }
        }

        /**
         * Converts the specified [BigInteger] to a string and appends to the provided builder.
         * This implements the recursive Schoenhage algorithm for base conversions.
         * The value must be non-negative!
         *
         * @param value the value to format to a string.
         * @param builder the [StringBuilder] to append to.
         * @param radix the base to convert to.
         * @param digits the minimum number of digits to pad to.
         */
        internal fun recursiveToString(value: BigInteger, builder: StringBuilder, radix: Int, digits: Int) {
            assert(radix in Character.MIN_RADIX..Character.MAX_RADIX)
            assert(value.signum() >= 0)

            // Use smallToString when it gets sufficiently small (recursion termination case.)
            if (value.words.size <= SCHOENHAGE_BASE_CONVERSION_THRESHOLD) {
                return smallToString(value, builder, radix, digits)
            }

            // Calculate a value for n in the equation radix^(2^n) = u and subtract 1 from that value.
            // This is used to find the cache index that contains the best value to divide u.
            val exponent = (ln(value.bitLength * PowerCache.LOG_TWO / PowerCache.getCachedLog(radix)) / PowerCache.LOG_TWO - 1.0)
                .roundToInt()

            val power = PowerCache.getCachedPower(radix, exponent)
            val (quotient, remainder) = value.divRem(power)

            val expectedDigits = 1 shl exponent

            // Now recursively build the two halves of each number.
            recursiveToString(quotient, builder, radix, digits - expectedDigits)
            recursiveToString(remainder, builder, radix, expectedDigits)
        }
    }

    /**
     * Helpers for treating big integers as int arrays in two's complement form.
     */
    object IntArrayHelpers {
        /**
         * Gets the length of the two's complement representation in ints,
         * including space for at least one sign bit.
         */
        private fun intLength(value: BigInteger): Int {
            return (value.bitLength ushr 5) + 1
        }

        /**
         * Returns an int of sign bits.
         */
        private fun signInt(value: BigInteger) = if (value.sign == Sign.Negative) -1 else 0

        /**
         * Two's complement helper to return one int of the result.
         *
         * @param n the index of the int to return. Int 0 is the least significant.
         *        This can be arbitrarily high - values are logically preceded by infinitely many sign ints.
         * @return the int.
         */
        internal fun getInt(value: BigInteger, n: Int): Int {
            if (n < 0) return 0
            if (n >= value.words.size) return signInt(value)

            val wordInt: Int = value.words[value.words.size - n - 1].toInt()
            return when {
                value.sign != Sign.Negative -> wordInt
                n <= value.indexOfFirstNonzeroInt -> -wordInt
                else -> wordInt.inv()
            }
        }

        /**
         * Converts the big integer to an int array.
         *
         * @see getInt
         */
        internal fun toIntArray(value: BigInteger, minimumSize: Int = value.words.size): IntArray {
            val result = IntArray(intLength(value).coerceAtLeast(minimumSize))

            for (i in result.indices) {
                result[result.size - i - 1] = getInt(value, i)
            }

            return result
        }

        /**
         * Creates a new big integer where one bit has been manipulated somehow.
         *
         * @param value the big integer.
         * @param n the bit index to manipulate.
         * @param manipulationOp the operation to perform. Receives the existing value, and
         *        an int indicating which bit to manipulate, returns the result.
         * @return the result.
         */
        internal fun manipulateBit(value: BigInteger, n: Int, manipulationOp: (Int, Int) -> Int): BigInteger {
            val intNum = n ushr 5

            val result = IntArray(intLength(value).coerceAtLeast(intNum + 2))
            for (i in result.indices) {
                result[result.size - i - 1] = getInt(value, i)
            }

            var intToModify = result[result.lastIndex - intNum]
            intToModify = manipulationOp(intToModify, 1 shl (n and 31))
            result[result.lastIndex - intNum] = intToModify

            return bigIntFromArray(result)
        }

        /**
         * Converts a two's complement int array to the equivalent positive uint array.
         */
        private fun makePositive(a: IntArray): UIntArray {
            var j: Int

            // Find first non-sign (0xffffffff) int of input
            var keep = 0
            while (keep < a.size && a[keep] == -1) {
                keep++
            }

            // Allocate output array.  If all non-sign ints are 0x00, we allocate space for one extra output int.
            j = keep
            while (j < a.size && a[j] == 0) {
                j++
            }
            val extraInt = if (j == a.size) 1 else 0
            val result = UIntArray(a.size - keep + extraInt)

            // Copy one's complement of input into output, leaving extra int (if it exists) == 0x00
            for (i in keep until a.size) {
                result[i - keep + extraInt] = a[i].inv().toUInt()
            }

            // Add one to one's complement to generate two's complement
            var i = result.size - 1
            while (++result[i] == 0U) {
                i--
            }

            return trimLeadingZeroes(result)
        }

        /**
         * Creates a [BigInteger] from an int array.
         *
         * @param value the int array.
         * @return the equivalent [BigInteger].
         * @see getInt
         */
        internal fun bigIntFromArray(value: IntArray): BigInteger {
            if (value.isEmpty()) {
                return ZERO
            }

            val words: UIntArray
            val sign: Sign

            if (value[0] < 0) {
                words = makePositive(value)
                sign = Sign.Negative
            } else {
                words = trimLeadingZeroes(value.toUIntArray())
                sign = if (words.isEmpty()) Sign.Zero else Sign.Positive
            }

            return BigInteger(sign = sign, words = words)
        }

        /**
         * Performs a bitwise unary op on a big integer.
         *
         * @param value the value.
         * @param intOp the operation to perform for individual ints.
         * @return the result.
         */
       internal fun bitwiseUnaryOp(value: BigInteger, intOp: (Int) -> Int): BigInteger {
            val resultSize = intLength(value)
            val result = IntArray(resultSize) { i ->
                val a = getInt(value, resultSize - i - 1)
                intOp(a)
            }
            return bigIntFromArray(result)
        }

        /**
         * Performs a bitwise binary op between two big integers.
         *
         * @param first the first value.
         * @param second the second value.
         * @param intOp the operation to perform for individual pairs of ints.
         * @return the result.
         */
        internal fun bitwiseBinaryOp(first: BigInteger, second: BigInteger, intOp: (Int, Int) -> Int): BigInteger {
            val resultSize = max(intLength(first), intLength(second))
            val result = IntArray(resultSize) { i ->
                val a = getInt(first, resultSize - i - 1)
                val b = getInt(second, resultSize - i - 1)
                intOp(a, b)
            }
            return bigIntFromArray(result)
        }
    }
}
