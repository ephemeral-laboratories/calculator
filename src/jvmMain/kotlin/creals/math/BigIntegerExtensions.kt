package garden.ephemeral.calculator.creals.math

fun Long.toBigInteger() = BigInteger.of(this)
fun Int.toBigInteger() = BigInteger.of(this)
fun Short.toBigInteger() = toInt().toBigInteger()
fun Byte.toBigInteger() = toInt().toBigInteger()
