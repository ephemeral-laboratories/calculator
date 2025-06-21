package garden.ephemeral.calculator.bigint

import java.math.BigInteger

fun Int.toBigInt(): BigInt = toLong().toBigInt()

fun Long.toBigInt(): BigInt = JavaMathBigInt(BigInteger.valueOf(this))
//fun Long.toBigInt(): BigInt = KLibsBigInt(bigIntOf(this))
//fun Long.toBigInt(): BigInt = KMathBigInt(this.toBigInt())
//fun Long.toBigInt(): BigInt = KtMathBigInt(BigInteger.of(this))
//fun Long.toBigInt(): BigInt = IonSpinBigInt(this.toBigInteger())

fun String.toBigInt(radix: Int): BigInt = JavaMathBigInt(BigInteger(this, radix))
//fun String.toBigInt(radix: Int): BigInt = KLibsBigInt(bigIntOf(this, radix))
//fun String.toBigInt(radix: Int): BigInt = KMathBigInt(this.toBigInt(radix))
//fun String.toBigInt(radix: Int): BigInt = KtMathBigInt(BigInteger.of(this, radix))
//fun String.toBigInt(radix: Int): BigInt = IonSpinBigInt(this.toBigInteger(base = radix))
