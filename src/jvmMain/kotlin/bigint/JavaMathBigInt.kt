package garden.ephemeral.calculator.bigint

import java.math.BigInteger

/**
 * java.math implementation of [BigInt].
 *
 * Currently working, but not my favourite implementation, because it blocks moving to KMP.
 */
@JvmInline
internal value class JavaMathBigInt(private val delegate: BigInteger) : BigInt {
    override fun signum() = delegate.signum()

    override fun unaryMinus() = (-delegate).wrap()
    override fun plus(other: BigInt) = (delegate + other.unwrap()).wrap()
    override fun minus(other: BigInt) = (delegate - other.unwrap()).wrap()
    override fun times(other: BigInt) = (delegate * other.unwrap()).wrap()
    override fun div(other: BigInt) = (delegate / other.unwrap()).wrap()
    override fun rem(other: BigInt) = (delegate % other.unwrap()).wrap()

    override fun divAndRem(other: BigInt): Pair<BigInt, BigInt> {
        val (quotient, remainder) = delegate.divideAndRemainder(other.unwrap())
        return Pair(quotient.wrap(), remainder.wrap())
    }

    override fun abs() = delegate.abs().wrap()
    override fun pow(exponent: Int) = delegate.pow(exponent).wrap()

    override fun bitLength() = delegate.bitLength()
    override fun inv() = delegate.inv().wrap()
    override fun and(other: BigInt) = (delegate and other.unwrap()).wrap()
    override fun or(other: BigInt) = (delegate or other.unwrap()).wrap()
    override fun xor(other: BigInt) = (delegate xor other.unwrap()).wrap()
    override fun shl(n: Int) = delegate.shl(n).wrap()
    override fun shr(n: Int) = delegate.shr(n).wrap()

    override fun toLong() = delegate.toLong()

    @Deprecated("Has obvious precision issues")
    override fun toDouble() = delegate.toDouble()

    override fun compareTo(other: BigInt) = delegate.compareTo(other.unwrap())

    override fun toString(): String = delegate.toString()
    override fun toString(radix: Int): String = delegate.toString(radix)

    companion object {
        private fun BigInteger.wrap(): BigInt = JavaMathBigInt(this)
        private fun BigInt.unwrap() = (this as JavaMathBigInt).delegate
    }
}
