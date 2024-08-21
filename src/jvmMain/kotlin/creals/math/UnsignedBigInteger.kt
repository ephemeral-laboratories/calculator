package garden.ephemeral.calculator.creals.math

/**
 * Arbitrarily big unsigned integer.
 *
 * This was extracted from [BigInteger] in order to separate the bit hacking logic
 * from the sign handling logic.
 *
 * The words are stored as [UInt]. `UInt` was chosen because some of the operations
 * we perform need to capture the overflow as a carry value, and had we used [ULong],
 * there would be no larger type to spill into in order to do this. Unfortunate,
 * because we think that `ULong` would perform better otherwise.
 *
 * @property words the words making up the number. Stored in raw binary,
 *           essentially base 2**32. Most significant words come first.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class UnsignedBigInteger private constructor(private val words: UIntArray) : Comparable<UnsignedBigInteger> {
    init {
        if (words.isNotEmpty()) {
            require(words[0] != 0U) { "words array contains leading zeroes: ${words.contentToString()}" }
        }
    }

    /**
     * Adds another [UnsignedBigInteger] to this one.
     *
     * @param other the other value.
     * @return the sum of the two values.
     */
    operator fun plus(other: UnsignedBigInteger): UnsignedBigInteger {
        return UnsignedBigInteger(addWordArrays(words, other.words))
    }

    /**
     * Subtracts another [UnsignedBigInteger] from this one.
     *
     * @param other the other value.
     * @return the difference of the two values.
     */
    operator fun minus(other: UnsignedBigInteger): UnsignedBigInteger {
        return UnsignedBigInteger(subtractWordArrays(words, other.words))
    }

    /**
     * Multiplies this [UnsignedBigInteger] with another one.
     *
     * @param other the other value.
     * @return the product of the two values.
     */
    operator fun times(other: UnsignedBigInteger): UnsignedBigInteger {
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

        val combinedWords = results.fold(uintArrayOf()) { acc, words -> addWordArrays(acc, words) }
        return UnsignedBigInteger(combinedWords)
    }

    /**
     * Divides this [UnsignedBigInteger] by another one.
     *
     * Note that it is more performant to call [divRem] as computing the quotient
     * and remainder at the same time is essentially no more expensive than computing one or
     * the other.
     *
     * @param other the other value.
     * @return the quotient.
     */
    operator fun div(other: UnsignedBigInteger) = divRem(other).quotient

    /**
     * Divides this [UnsignedBigInteger] by another one, keeping the remainder.
     *
     * Note that it is more performant to call [divRem] as computing the quotient
     * and remainder at the same time is essentially no more expensive than computing one or
     * the other.
     *
     * @param other the other value.
     * @return the remainder.
     */
    operator fun rem(other: UnsignedBigInteger) = divRem(other).remainder

    /**
     * Divides this [UnsignedBigInteger] by another one, computing the quotient
     * and the remainder at the same time.
     *
     * @param other the other value.
     * @return the quotient and remainder.
     */
    fun divRem(other: UnsignedBigInteger): QuotientWithRemainder {
        val dividendWords: UIntArray = words
        val divisorWords: UIntArray = other.words
        if (divisorWords.isEmpty()) {
            throw ArithmeticException("Division by zero")
        }

        val comparison = compareTo(other)
        if (comparison < 0) {
            // dividend is smaller than the divisor.
            return QuotientWithRemainder(ZERO, this)
        } else if (comparison == 0) {
            // dividend is the same as the divisor.
            return QuotientWithRemainder(ONE, ZERO)
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
            UnsignedBigInteger(trimLeadingZeroes(quotientWords)),
            UnsignedBigInteger(trimLeadingZeroes(remainder)),
        )
    }

    /**
     * Shifts left the given number of bits.
     *
     * This parameter is considered signed - if a negative value is provided, it will shift right instead.
     *
     * @param n the number of bits to shift left.
     * @return the result.
     */
    infix fun shl(n: Int): UnsignedBigInteger = when {
        n == 0 -> this
        n < 0 -> shr(-n)
        else -> UnsignedBigInteger(words = shiftWordArrayLeft(words, n))
    }

    /**
     * Shifts right the given number of bits.
     *
     * This parameter is considered signed - if a negative value is provided, it will shift left instead.
     *
     * @param n the number of bits to shift right.
     * @return the result.
     */
    infix fun shr(n: Int): UnsignedBigInteger = when {
        n == 0 -> this
        n < 0 -> shl(-n)
        else -> UnsignedBigInteger(words = shiftWordArrayRight(words, n))
    }

    /**
     * Compares with another [UnsignedBigInteger] for ordering.
     *
     * Compares the sizes first - a longer array is always a larger value.
     * If the sizes are equal, compares each corresponding word, starting with the most
     * significant words, which are stored first.
     *
     * @param other the other value to compare against.
     * @return `1` if our value is greater,
     *         `-1` if the other value is greater,
     *         `0` if the two are equal.
     */
    override operator fun compareTo(other: UnsignedBigInteger) = compareWordArrays(words, other.words)

    override fun equals(other: Any?) =
        (other is UnsignedBigInteger) && words.contentEquals(other.words)

    override fun hashCode() = words.contentHashCode()

    override fun toString() = words.contentToString()

    companion object {
        private const val STORAGE_BASE_LOG2 = 32
        private val STORAGE_BASE = 1UL shl STORAGE_BASE_LOG2
        private const val MAX_WORD_ARRAY_SIZE = Int.MAX_VALUE / Integer.SIZE + 1 // (1 << 26)

        fun of(value: String, radix: Int = 10): UnsignedBigInteger {
            require(value.isNotEmpty()) { "value must not be empty" }
            require(value.matches(Regex("^[0-9_]+$"))) { "value must be a number" }

            var charIndex = 0

            // Simple approach of reusing the existing * and + operations to build up the value as we parse it.
            // The rest BigInteger's equivalent method has some smarts which make it run faster, by processing
            // multiple digits in a single pass.
            var accumulator = ZERO
            val bigRadix = of(radix.toUInt())
            while (charIndex < value.length) {
                val ch = value[charIndex]
                if (ch != '_') {
                    val digit = Character.digit(ch, radix).toUInt()
                    accumulator = accumulator * bigRadix + of(digit)
                }
                charIndex++
            }

            return accumulator
        }

        fun of(value: UInt): UnsignedBigInteger {
            val words = if (value == 0U) {
                uintArrayOf()
            } else {
                uintArrayOf(value)
            }
            return UnsignedBigInteger(words = words)
        }

        fun of(value: ULong): UnsignedBigInteger {
            val words = if (value == 0UL) {
                uintArrayOf()
            } else {
                val highWord = (value shr 32).toUInt()
                val lowWord = value.toUInt()
                when {
                    highWord == 0U -> uintArrayOf(lowWord)
                    else -> uintArrayOf(highWord, lowWord)
                }
            }
            return UnsignedBigInteger(words = words)
        }

        val ZERO: UnsignedBigInteger = UnsignedBigInteger(words = uintArrayOf())
        val ONE: UnsignedBigInteger = UnsignedBigInteger(words = uintArrayOf(1U))

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

    data class QuotientWithRemainder(
        val quotient: UnsignedBigInteger,
        val remainder: UnsignedBigInteger,
    )
}
