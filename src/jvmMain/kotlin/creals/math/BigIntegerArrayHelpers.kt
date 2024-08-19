package garden.ephemeral.calculator.creals.math

import garden.ephemeral.calculator.creals.math.BigInteger.Companion.STORAGE_BASE
import garden.ephemeral.calculator.creals.math.BigInteger.Companion.STORAGE_BASE_LOG2

@OptIn(ExperimentalUnsignedTypes::class)
internal object BigIntegerArrayHelpers {

    private fun presentResultDigits(accumulator: MutableList<UInt>): UIntArray {
        // Trim trailing zeroes. These would become leading zeroes once the list is reversed,
        // and we can't allow for the possibility of there being more than one way to represent
        // a single numeric value.
        while (accumulator.isNotEmpty() && accumulator[accumulator.lastIndex] == 0U) {
            accumulator.removeLast()
        }
        return accumulator.reversed().toUIntArray()
    }

    /**
     * Adds two arrays of digits.
     *
     * @param leftAddendDigits the left digits.
     * @param rightAddendDigits the right digits.
     * @return the sum of the two values, as a new array of digits.
     */
    internal fun add(leftAddendDigits: UIntArray, rightAddendDigits: UIntArray): UIntArray {
        val result = mutableListOf<UInt>()
        var carry = 0UL
        var leftIndex = leftAddendDigits.lastIndex
        var rightIndex = rightAddendDigits.lastIndex
        while (leftIndex >= 0 || rightIndex >= 0 || carry > 0UL) {
            val leftDigit = if (leftIndex >= 0) leftAddendDigits[leftIndex].toULong() else 0UL
            val rightDigit = if (rightIndex >= 0) rightAddendDigits[rightIndex].toULong() else 0UL
            val sum = leftDigit + rightDigit + carry

            result += sum.toUInt()
            carry = sum shr STORAGE_BASE_LOG2

            leftIndex--
            rightIndex--
        }
        return presentResultDigits(result)
    }

    /**
     * Subtracts an array of digits from another.
     *
     * @param minuendDigits the minuend digits.
     * @param subtrahendDigits the subtrahend digits.
     * @return the result of subtracting the right digits from the left digits (the difference),
     *         as a new array of digits.
     */
    internal fun subtract(minuendDigits: UIntArray, subtrahendDigits: UIntArray): UIntArray {
        val result = mutableListOf<UInt>()
        var carry = 0UL
        var minuendIndex = minuendDigits.lastIndex
        var subtrahendIndex = subtrahendDigits.lastIndex
        while (minuendIndex >= 0 || subtrahendIndex >= 0 || carry > 0UL) {
            var minuendDigit = if (minuendIndex >= 0) minuendDigits[minuendIndex].toULong() else 0UL
            val subtrahendDigit = if (subtrahendIndex >= 0) subtrahendDigits[subtrahendIndex].toULong() else 0UL

            if (minuendDigit < subtrahendDigit) {
                minuendDigit += STORAGE_BASE
            }

            val difference = minuendDigit - subtrahendDigit - carry
            result += difference.toUInt()
            carry = if (minuendDigit >= STORAGE_BASE) 1UL else 0UL

            minuendIndex--
            subtrahendIndex--
        }
        return presentResultDigits(result)
    }

    /**
     * Multiplies two arrays of digits.
     *
     * @param leftFactorDigits the left digits.
     * @param rightFactorDigits the right digits.
     * @return the product of the two values, as a new array of digits.
     */
    internal fun multiply(leftFactorDigits: UIntArray, rightFactorDigits: UIntArray): UIntArray {
        val results = mutableListOf<UIntArray>()
        var carry = 0UL
        val leftIndex = leftFactorDigits.lastIndex
        val rightIndex = rightFactorDigits.lastIndex

        for (i in rightIndex downTo 0) {
            val currentResult = mutableListOf<UInt>()
            val rightDigit = rightFactorDigits[i].toULong()
            for (j in leftIndex downTo 0) {
                val leftDigit = leftFactorDigits[j].toULong()
                val sum = leftDigit * rightDigit + carry

                currentResult += sum.toUInt()
                carry = sum shr STORAGE_BASE_LOG2
            }
            if (carry > 0UL) {
                currentResult += carry.toUInt()
                carry = 0UL
            }
            results.add(presentResultDigits(currentResult))
        }

        return results.fold(uintArrayOf()) { acc, digits -> add(acc, digits) }
    }

    /**
     * Divides an array of digits by another.
     *
     * @param dividendDigits the dividend digits.
     * @param divisorDigits the divisor digits.
     * @return the result of dividing the dividend by the divisor (the quotient),
     *         as well as the remainder, as new arrays of digits.
     */
    internal fun divide(dividendDigits: UIntArray, divisorDigits: UIntArray): Pair<UIntArray, UIntArray> {
        val result = mutableListOf<UInt>()

        if (divisorDigits.isEmpty()) {
            throw ArithmeticException("Division by zero")
        }

        var count = 0U
        while (compareDigits(dividendDigits, multiply(divisorDigits, uintArrayOf(count + 1U))) >= 0) {
            count++
        }

        val remainder = subtract(dividendDigits, multiply(divisorDigits, uintArrayOf(count)))
        result.add(count)

        return Pair(presentResultDigits(result), remainder)
    }

    /**
     * Compares two arrays of digits for ordering.
     *
     * Compares the sizes first - a longer array is always a larger value.
     * If the sizes are equal, compares each corresponding digit, starting with the most
     * significant digits, which are stored first.
     *
     * @param leftDigits the digits on the left of the comparison.
     * @param rightDigits the digits on the right of the comparison.
     * @return `1` if the digits on the left are greater,
     *         `-1` if the digits on the right are greater,
     *         `0` if the digits are equal.
     */
    internal fun compareDigits(leftDigits: UIntArray, rightDigits: UIntArray): Int {
        val sizeComparison = leftDigits.size.compareTo(rightDigits.size)
        if (sizeComparison != 0) {
            return sizeComparison
        }
        leftDigits.indices.forEach { index ->
            val digitComparison = leftDigits[index].compareTo(rightDigits[index])
            if (digitComparison != 0) {
                return digitComparison
            }
        }
        return 0
    }
}
