package garden.ephemeral.calculator.bigint

class TwoImplBigInt(private val impl1: BigInt, private val impl2: BigInt) : BigInt {
    private fun <T> assertEqual(value1: T, value2: T, message: () -> String) {
        if (value1 != value2) {
            throw AssertionError("Results differ ($value1 != $value2) for operation: ${message()}")
        }
    }

    override fun signum(): Int {
        val result1 = impl1.signum()
        val result2 = impl2.signum()
        assertEqual(result1, result2) { "$this.signum()" }
        return result1
    }

    override fun unaryMinus(): BigInt {
        val result1 = impl1.unaryMinus()
        val result2 = impl2.unaryMinus()
        assertEqual(result1.toString(), result2.toString()) { "$this.unaryMinus()" }
        return TwoImplBigInt(result1, result2)
    }

    override fun plus(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.plus(other.impl1)
        val result2 = impl2.plus(other.impl2)
        assertEqual(result1.toString(), result2.toString()) { "$this.plus($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun minus(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.minus(other.impl1)
        val result2 = impl2.minus(other.impl2)
        assertEqual(result1.toString(), result2.toString()) { "$this.minus($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun times(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.times(other.impl1)
        val result2 = impl2.times(other.impl2)
        assertEqual(result1.toString(), result2.toString()) { "$this.times($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun div(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.div(other.impl1)
        val result2 = impl2.div(other.impl2)
        assertEqual(result1.toString(), result2.toString()) { "$this.div($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun rem(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.rem(other.impl1)
        val result2 = impl2.rem(other.impl2)
        assertEqual(result1.toString(), result2.toString()) {"$this.rem($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun divAndRem(other: BigInt): Pair<BigInt, BigInt> {
        other as TwoImplBigInt
        val (quotient1, remainder1) = impl1.divAndRem(other.impl1)
        val (quotient2, remainder2) = impl2.divAndRem(other.impl2)
        assertEqual(quotient1.toString(), quotient2.toString()) {"$this.divAndRem($other)"}
        assertEqual(remainder1.toString(), remainder2.toString()) {"$this.divAndRem($other)"}
        return Pair(TwoImplBigInt(quotient1, quotient2), TwoImplBigInt(remainder1, remainder2))
    }

    override fun abs(): BigInt {
        val result1 = impl1.abs()
        val result2 = impl2.abs()
        assertEqual(result1.toString(), result2.toString()) {"$this.abs()"}
        return TwoImplBigInt(result1, result2)
    }

    override fun pow(exponent: Int): BigInt {
        val result1 = impl1.pow(exponent)
        val result2 = impl2.pow(exponent)
        assertEqual(result1.toString(), result2.toString()) {"$this.pow($exponent)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun bitLength(): Int {
        val result1 = impl1.bitLength()
        val result2 = impl2.bitLength()
        assertEqual(result1, result2) {"$this.bitLength()"}
        return result1
    }

    override fun inv(): BigInt {
        val result1 = impl1.inv()
        val result2 = impl2.inv()
        assertEqual(result1.toString(), result2.toString()) {"$this.inv()"}
        return TwoImplBigInt(result1, result2)
    }

    override fun and(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.and(other.impl1)
        val result2 = impl2.and(other.impl2)
        assertEqual(result1.toString(), result2.toString()) {"$this.and($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun or(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.or(other.impl1)
        val result2 = impl2.or(other.impl2)
        assertEqual(result1.toString(), result2.toString()) {"$this.or($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun xor(other: BigInt): BigInt {
        other as TwoImplBigInt
        val result1 = impl1.xor(other.impl1)
        val result2 = impl2.xor(other.impl2)
        assertEqual(result1.toString(), result2.toString()) {"$this.xor($other)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun shl(n: Int): BigInt {
        val result1 = impl1.shl(n)
        val result2 = impl2.shl(n)
        assertEqual(result1.toString(), result2.toString()) {"$this.shl($n)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun shr(n: Int): BigInt {
        val result1 = impl1.shr(n)
        val result2 = impl2.shr(n)
        assertEqual(result1.toString(), result2.toString()) {"$this.shr($n)"}
        return TwoImplBigInt(result1, result2)
    }

    override fun toLong(): Long {
        val result1 = impl1.toLong()
        val result2 = impl2.toLong()
        assertEqual(result1, result2) {"$this.toLong()"}
        return result1
    }

    override fun toDouble(): Double {
        val result1 = impl1.toDouble()
        val result2 = impl2.toDouble()
        assertEqual(result1, result2) {"$this.toDouble()"}
        return result1
    }

    override fun compareTo(other: BigInt): Int {
        other as TwoImplBigInt
        val result1 = impl1.compareTo(other.impl1)
        val result2 = impl2.compareTo(other.impl2)
        assertEqual(result1, result2) { "$this.compareTo($other)"}
        return result1
    }

    override fun toString(radix: Int): String {
        return impl1.toString(radix)
    }

    override fun toString(): String {
        return impl1.toString()
    }
}