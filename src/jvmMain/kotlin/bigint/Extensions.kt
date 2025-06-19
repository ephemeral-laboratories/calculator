package garden.ephemeral.calculator.bigint

import java.math.BigInteger

fun Int.toBigInt(): BigInt = toLong().toBigInt()

fun Long.toBigInt(): BigInt = JavaMathBigInt(BigInteger.valueOf(this))
//fun Long.toBigInt(): BigInt = KLibsBigInt(bigIntOf(this))

fun String.toBigInt(radix: Int): BigInt = JavaMathBigInt(BigInteger(this, radix))
//fun String.toBigInt(radix: Int): BigInt = KLibsBigInt(bigIntOf(this, radix))
