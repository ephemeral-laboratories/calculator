package garden.ephemeral.calculator.bigint

/**
 * Big integer abstraction.
 *
 * We abstract away the big integer implementation because we have tried,
 * so far, four different implementations. There is little confidence that
 * I will remain on the same one forever.
 */
interface BigInt : Comparable<BigInt> {
    /**
     * Determines the sign of this value.
     *
     * @return -1, 0, or 1, if the value is negative, zero, or positive, respectively.
     */
    fun signum(): Int

    /**
     * Negates this value.
     *
     * @return the negated value.
     */
    operator fun unaryMinus(): BigInt

    /**
     * Returns this value.
     *
     * @return this value.
     */
    operator fun unaryPlus(): BigInt = this

    /**
     * Adds `other` to this value.
     *
     * @param other the value to add.
     * @return the sum.
     */
    operator fun plus(other: BigInt): BigInt

    /**
     * Subtracts `other` from this value.
     *
     * @param other the value to subtract.
     * @return the difference.
     */
    operator fun minus(other: BigInt): BigInt

    /**
     * Multiplies this value by `other`.
     *
     * @param other the value to multiply by.
     * @return the product.
     */
    operator fun times(other: BigInt): BigInt

    /**
     * Divides this value by `other`.
     *
     * @param other the value to divide by.
     * @return the quotient.
     */
    operator fun div(other: BigInt): BigInt

    /**
     * Divides this value by `other`, returning the remainder.
     *
     * @param other the value to divide by.
     * @return the remainder.
     */
    operator fun rem(other: BigInt): BigInt

    /**
     * Divides this value by `other`, returning both the quotient and the remainder.
     *
     * @param other the value to divide by.
     * @return the quotient and remainder.
     */
    fun divAndRem(other: BigInt): Pair<BigInt, BigInt>

    /**
     * Gets the absolute value of this value.
     *
     * @return the absolute value.
     */
    fun abs(): BigInt

    /**
     * Raises this value to the power `exponent`.
     *
     * @param exponent the exponent.
     * @return the result.
     * @throws ArithmeticException if `exponent` is negative.
     */
    fun pow(exponent: Int): BigInt

    /**
     * Computes the number of bits required to store this big integer.
     *
     * For positive numbers, returns the number of bits.
     * For negative numbers, returns the number of bits in the two's complement representation.
     * For 0, returns 0.
     *
     * @return the number of bits.
     */
    fun bitLength(): Int

    /**
     * Performs bitwise NOT.
     */
    fun inv(): BigInt

    /**
     * Performs bitwise AND with `other`.
     */
    infix fun and(other: BigInt): BigInt

    /**
     * Performs bitwise OR with `other`.
     */
    infix fun or(other: BigInt): BigInt

    /**
     * Performs bitwise XOR with `other`.
     */
    infix fun xor(other: BigInt): BigInt

    /**
     * Shift left `n` bits.
     *
     * @param n the number of bits to shift. If negative, will shift right.
     * @return the shifted result.
     */
    infix fun shl(n: Int): BigInt

    /**
     * Shift right `n` bits.
     *
     * @param n the number of bits to shift. If negative, will shift left.
     * @return the shifted result.
     */
    infix fun shr(n: Int): BigInt

    fun toLong(): Long

    fun toInt() = toLong().toInt()

    fun toShort() = toLong().toShort()

    fun toByte() = toLong().toByte()

    @Deprecated("Has obvious precision issues")
    fun toDouble(): Double

    @Deprecated("Has obvious precision issues")
    fun toFloat() = toDouble().toFloat()

    /**
     * Multiply by `2**n`, rounding result
     */
    fun scale(n: Int): BigInt {
        if (n >= 0) {
            return this shl n
        } else {
            val adjK = (this shl (n + 1)) + One
            return adjK shr 1
        }
    }

    /**
     * Converts to a string in a specific base.
     *
     * @param radix the base to use. Must be between 2 and 36.
     * @return the string representation of the number in the given base.
     */
    fun toString(radix: Int): String

    companion object {
        val Zero: BigInt = 0.toBigInt()
        val One: BigInt = 1.toBigInt()

        val MinusOne: BigInt = (-1).toBigInt()

    }
}
