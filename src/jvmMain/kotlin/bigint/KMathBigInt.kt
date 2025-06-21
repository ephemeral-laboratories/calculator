package garden.ephemeral.calculator.bigint

///**
// * Implementation of big integer on top of kmath-core's BigInt.
// */
//@JvmInline
//value class KMathBigInt(private val delegate: KMBigInt) : BigInt {
//    override fun signum(): Int {
//        TODO()
//    }
//
//    override fun unaryMinus() = (-unwrap()).wrap()
//    override fun plus(other: BigInt) = (unwrap() + other.unwrap()).wrap()
//    override fun minus(other: BigInt) = (unwrap() - other.unwrap()).wrap()
//    override fun times(other: BigInt) = (unwrap() * other.unwrap()).wrap()
//    override fun div(other: BigInt) = (unwrap() / other.unwrap()).wrap()
//    override fun rem(other: BigInt) = (unwrap() % other.unwrap()).wrap()
//
//    // Operation missing from KMath but we can do it the slow way.
//    override fun divAndRem(other: BigInt) = Pair(
//        (unwrap() / other.unwrap()).wrap(),
//        (unwrap() % other.unwrap()).wrap()
//    )
//
//    override fun abs() = unwrap().abs().wrap()
//
//    override fun pow(exponent: Int): BigInt {
//        if (exponent < 0) {
//            throw ArithmeticException("Negative exponent")
//        }
//        return unwrap().pow(exponent.toUInt()).wrap()
//    }
//
//    override fun bitLength(): Int {
//        TODO("bitLength missing from KMath")
//    }
//
//    override fun inv(): BigInt {
//        TODO("inv missing from KMath")
//    }
//
//    override fun and(other: BigInt) = (unwrap() and other.unwrap()).wrap()
//    override fun or(other: BigInt) = (unwrap() or other.unwrap()).wrap()
//
//    override fun xor(other: BigInt): BigInt {
//        TODO("xor missing from KMath")
//    }
//
//    override fun shl(n: Int) = (unwrap() shl n).wrap()
//    override fun shr(n: Int) = (unwrap() shr n).wrap()
//
//    override fun toLong(): Long {
//        TODO("toLong missing from KMath")
//    }
//
//    override fun toDouble(): Double {
//        TODO("toDouble missing from KMath")
//    }
//
//    override fun compareTo(other: BigInt) = unwrap().compareTo(other.unwrap())
//
//    override fun toString(radix: Int): String {
//        TODO("toString with radix missing from KMath")
//    }
//
//    override fun toString(): String {
//        TODO("toString with radix missing from KMath")
//
//    }
//
//    companion object {
//        private fun KMBigInt.wrap(): BigInt = KMathBigInt(this)
//        private fun BigInt.unwrap() = (this as KMathBigInt).delegate
//    }
//}
