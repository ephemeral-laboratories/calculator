package garden.ephemeral.calculator.creals.math

/**
 * Arbitrarily big integer.
 *
 * @property sign the sign of the number.
 * @property words multiple words making up the number. Most significant words come first.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class BigInteger private constructor(private val sign: Sign, private val words: UIntArray) : Comparable<BigInteger> {
    init {
        if (words.isNotEmpty()) {
            require(words[0] != 0U) { "words array contains leading zeroes: ${words.contentToString()}" }
        }
    }

    /**
     * Returns the signum function for this [BigInteger].
     * This is a little awkward because we have chosen not to represent 0 as a sign value.
     *
     * @return `-1`, `0` or `1` if this value is negative, zero or positive.
     */
    fun signum(): Int = when {
        bitLength == 0 -> 0
        sign == Sign.NEGATIVE -> -1
        else -> 1
    }

    /**
     * Gets the absolute value of this [BigInteger].
     *
     * @return the absolute value.
     */
    fun abs(): BigInteger = when {
        sign == Sign.NEGATIVE -> -this
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
        return if (sign == other.sign) {
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
        return if (sign == other.sign) {
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
        val leftFactorWords = words
        val rightFactorWords = other.words

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

        return BigInteger(
            sign = if (sign == other.sign) Sign.POSITIVE else Sign.NEGATIVE,
            words = results.fold(uintArrayOf()) { acc, words -> addWordArrays(acc, words) },
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
        val dividendWords = words
        val divisorWords = other.words
        if (divisorWords.isEmpty()) {
            throw ArithmeticException("Division by zero")
        }

        val quotientSign = if (sign == other.sign) Sign.POSITIVE else Sign.NEGATIVE
        val remainderSign = sign

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
        var remainder = uintArrayOf()

        for (wordIndex in dividendWords.indices) {
            var startIndexWithinWord = 31
            if (wordIndex == 0) {
                startIndexWithinWord -= dividendWords[0].countLeadingZeroBits()
            }
            var quotientWord = 0U
            for (indexWithinWord in startIndexWithinWord downTo 0) {
                // Append the current bit of the dividend to the remainder
                val currentBitOfDividend = (dividendWords[wordIndex] shr indexWithinWord) and 1U
                remainder = shiftWordArrayLeft(remainder, 1)
                if (remainder.isEmpty()) {
                    remainder = uintArrayOf(0U)
                }
                remainder[remainder.lastIndex] = remainder[remainder.lastIndex] or currentBitOfDividend

                // Can we subtract the divisor yet?
                if (compareWordArrays(remainder, divisorWords) >= 0) {
                    remainder = subtractWordArrays(remainder, divisorWords)
                    quotientWord = quotientWord or (1U shl indexWithinWord)
                }
            }
            quotientWords[wordIndex] = quotientWord
        }

        return QuotientWithRemainder(
            BigInteger(sign = quotientSign, words = trimLeadingZeroes(quotientWords)),
            BigInteger(sign = remainderSign, words = trimLeadingZeroes(remainder)),
        )
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
        else -> BigInteger(sign = sign, words = shiftWordArrayRight(words, n))
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
        val wordCount = words.size
        var bitLength = if (wordCount == 0) {
            0
        } else {
            STORAGE_BASE_LOG2 * wordCount - words[0].countLeadingZeroBits()
        }
        if (bitLength > 0 && sign == Sign.NEGATIVE) {
            val magnitudeIsPowerOf2 = bitCount == 1
            if (magnitudeIsPowerOf2) {
                bitLength -= 1
            }
        }
        bitLength
    }

    /**
     * Gets the number of bits in the two's complement representation of this [BigInteger]
     * that differ from its sign.
     * This method is useful when implementing bit-vector style sets.
     *
     * @return the number of bits in the two's complement representation of this [BigInteger]
     *         that differ from its sign\.
     */
    val bitCount: Int by lazy {
        var bitCount = words.sumOf { word -> word.countOneBits() }
        if (sign == Sign.NEGATIVE) {
            bitCount += trailingZeroCount() - 1
        }
        bitCount
    }

    private fun trailingZeroCount(): Int {
        var magTrailingZeroCount = 0
        var j: Int = words.lastIndex
        while (words[j] == 0U) {
            magTrailingZeroCount += 32
            j--
        }
        magTrailingZeroCount += words[j].countTrailingZeroBits()
        return magTrailingZeroCount
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
        val signComparison = this.sign.compareTo(other.sign)
        if (signComparison != 0) {
            return signComparison
        }

        var wordsComparison = compareWordArrays(words, other.words)
        if (wordsComparison != 0) {
            // Sort small negative values above large negative values
            if (sign == Sign.NEGATIVE) {
                wordsComparison = -wordsComparison
            }
            return wordsComparison
        }

        return 0
    }

    override fun equals(other: Any?) =
        (other is BigInteger) && sign == other.sign && words.contentEquals(other.words)

    override fun hashCode() = sign.hashCode() * 31 + words.contentHashCode()

    // Misc

    override fun toString(): String {
        return "BigInteger[sign=$sign, words=${words.contentToString()}]"
    }

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

            return BigInteger(sign = sign, words = accumulator.words)
        }

        fun of(value: Int): BigInteger {
            val words = when {
                value == 0 -> uintArrayOf()
                value < 0 -> return -of(-value)
                else -> uintArrayOf(value.toUInt())
            }
            return BigInteger(sign = Sign.POSITIVE, words = words)
        }

        fun of(value: UInt): BigInteger {
            val words = when (value) {
                0U -> uintArrayOf()
                else -> uintArrayOf(value)
            }
            return BigInteger(sign = Sign.POSITIVE, words = words)
        }

        fun of(value: Long): BigInteger {
            val words = when {
                value == 0L -> uintArrayOf()
                value < 0L -> return -of(-value)
                value <= UInt.MAX_VALUE.toLong() -> uintArrayOf(value.toUInt())
                else -> uintArrayOf((value shr 32).toUInt(), value.toUInt())
            }
            return BigInteger(sign = Sign.POSITIVE, words = words)
        }

        fun of(value: ULong): BigInteger {
            val words = when {
                value == 0UL -> uintArrayOf()
                value <= UInt.MAX_VALUE.toULong() -> uintArrayOf(value.toUInt())
                else -> uintArrayOf((value shr 32).toUInt(), value.toUInt())
            }
            return BigInteger(sign = Sign.POSITIVE, words = words)
        }

        val ZERO = BigInteger(sign = Sign.POSITIVE, words = uintArrayOf())
        val ONE = BigInteger(sign = Sign.POSITIVE, words = uintArrayOf(1U))

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
     * Enumeration of signs for a number.
     * Usually you would also see a zero sign for the 0 case - this class treats zero
     * as positive.
     */
    enum class Sign {
        // Negative comes first so that `compareTo` works sensibly.
        NEGATIVE,
        POSITIVE,
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
