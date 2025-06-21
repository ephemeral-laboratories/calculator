package garden.ephemeral.calculator.bigint

///**
// * Implementation of big integer on top of kt-math's BigInteger.
// */
//@JvmInline
//value class KtMathBigInt(private val delegate: BigInteger) : BigInt {
//    override fun signum() = unwrap().signum
//
//    override fun unaryMinus() = (-unwrap()).wrap()
//    override fun plus(other: BigInt) = (unwrap() + other.unwrap()).wrap()
//    override fun minus(other: BigInt) = (unwrap() - other.unwrap()).wrap()
//    override fun times(other: BigInt) = (unwrap() * other.unwrap()).wrap()
//    override fun div(other: BigInt) = (unwrap() / other.unwrap()).wrap()
//    override fun rem(other: BigInt) = (unwrap() % other.unwrap()).wrap()
//
//    override fun divAndRem(other: BigInt): Pair<BigInt, BigInt> {
//        val (quotient, remainder) = unwrap().divideAndRemainder(other.unwrap())
//        return Pair(quotient.wrap(), remainder.wrap())
//    }
//
//    override fun abs() = unwrap().absoluteValue.wrap()
//    override fun pow(exponent: Int) = unwrap().pow(exponent).wrap()
//
//    override fun bitLength() = unwrap().bitLength
//    override fun inv() = unwrap().not().wrap()
//    override fun and(other: BigInt) = (unwrap().and(other.unwrap())).wrap()
//    override fun or(other: BigInt) = (unwrap().or(other.unwrap())).wrap()
//    override fun xor(other: BigInt) = (unwrap().xor(other.unwrap())).wrap()
//    override fun shl(n: Int) = (unwrap() shl n).wrap()
//    override fun shr(n: Int) = (unwrap() shr n).wrap()
//
//    override fun toLong() = unwrap().toLong()
//
//    @Deprecated("Has obvious precision issues")
//    override fun toDouble() = unwrap().toDouble()
//
//    override fun compareTo(other: BigInt) = unwrap().compareTo(other.unwrap())
//
//    override fun toString(): String = unwrap().toString()
//    override fun toString(radix: Int): String = unwrap().toString(radix)
//
//    companion object {
//        private fun BigInteger.wrap(): BigInt = KtMathBigInt(this)
//        private fun BigInt.unwrap() = (this as KtMathBigInt).delegate
//    }
//}
