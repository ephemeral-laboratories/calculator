package garden.ephemeral.calculator.bigint

///**
// * Implementation of big integer on top of k-labs' big-numbers library.
// */
//class KLibsBigInt(private val delegate: KLBigInt) : BigInt {
//    override fun signum() = when {
//        delegate.isPositive -> 1
//        delegate.isNegative -> -1
//        // delegate.isZero -> 0
//        else -> 0
//    }
//
//    override fun unaryMinus() = (-delegate).wrap()
//    override fun plus(other: BigInt) = (delegate + other.unwrap()).wrap()
//    override fun minus(other: BigInt) = (delegate - other.unwrap()).wrap()
//    override fun times(other: BigInt) = (delegate * other.unwrap()).wrap()
//    override fun div(other: BigInt) = (delegate / other.unwrap()).wrap()
//    override fun rem(other: BigInt) = (delegate % other.unwrap()).wrap()
//
//    override fun divAndRem(other: BigInt): Pair<BigInt, BigInt> {
//        val (quotient, remainder) = delegate.divideAndRemainder(other.unwrap())
//        return Pair(quotient.wrap(), remainder.wrap())
//    }
//
//    override fun abs() = delegate.abs().wrap()
//    override fun pow(exponent: Int) = delegate.pow(exponent).wrap()
//
//    override fun bitLength() = delegate.bitLength()
//    override fun inv() = delegate.inv().wrap()
//    override fun and(other: BigInt) = (delegate and other.unwrap()).wrap()
//    override fun or(other: BigInt) = (delegate or other.unwrap()).wrap()
//    override fun xor(other: BigInt) = (delegate xor other.unwrap()).wrap()
//    override fun shl(n: Int) = delegate.shl(n).wrap()
//    override fun shr(n: Int) = delegate.shr(n).wrap()
//
//    override fun toLong() = delegate.toLong()
//
//    // TODO: Does converting via Long work?
//    @Deprecated("Has obvious precision issues")
//    override fun toDouble() = TODO("This library lacks toDouble() - so now what?")
//
//    override fun compareTo(other: BigInt) = (this - other).signum()
//
//    override fun toString(): String = delegate.toString()
//    override fun toString(radix: Int): String = delegate.toString(radix)
//
//    companion object {
//        private fun KLBigInt.wrap(): BigInt = KLibsBigInt(this)
//        private fun BigInt.unwrap() = (this as KLibsBigInt).delegate
//    }
//}
