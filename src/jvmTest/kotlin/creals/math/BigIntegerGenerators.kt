package garden.ephemeral.calculator.creals.math

import io.kotest.property.Arb
import io.kotest.property.Classifier
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.ArbitraryBuilder
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.random.Random

private fun randomBigInteger(rand: Random, range: ClosedRange<BigInteger>): BigInteger {
    fun BigInteger.toBigDecimal() = BigDecimal(this.toString())
    fun BigDecimal.toOurBigInteger() = BigInteger.of(this.toString())

    val rangeStart = range.start
    val rangeEnd = range.endInclusive
    val scale = rangeEnd.toString().length
    var generated = ""
    for (i in 0 until scale) {
        generated += rand.nextInt(10)
    }
    val inputRangeStart = BigDecimal.ZERO.setScale(scale, RoundingMode.FLOOR)
    val inputRangeEnd = BigDecimal(String.format("%0${scale}d", 0).replace('0', '9'))
        .setScale(scale, RoundingMode.FLOOR)
    val outputRangeStart = rangeStart.toBigDecimal().setScale(scale, RoundingMode.FLOOR)
    // Adds one to the output range to correct rounding
    val outputRangeEnd = (rangeEnd.toBigDecimal() + BigDecimal.ONE).setScale(scale, RoundingMode.FLOOR)


    val result = ((generated.toBigDecimal().setScale(scale, RoundingMode.FLOOR) - inputRangeStart)
        .divide(inputRangeEnd - inputRangeStart, RoundingMode.FLOOR) *
            (outputRangeEnd - outputRangeStart) + outputRangeStart)
        .setScale(0, RoundingMode.FLOOR)

    return result.toOurBigInteger().coerceAtMost(rangeEnd)
}

private fun BigInteger.isEven() = rem(BigInteger.of(2)).signum() == 0

private class BigIntegerShrinker(val range: ClosedRange<BigInteger>) : Shrinker<BigInteger> {
    override fun shrink(value: BigInteger): List<BigInteger> = when (value) {
        BigInteger.ZERO -> emptyList()
        BigInteger.of(1), BigInteger.of(-1) -> listOf(BigInteger.ZERO).filter { it in range }
        else -> {
            val a = listOf(
                BigInteger.ZERO,
                BigInteger.of(1),
                BigInteger.of(-1),
                value.abs(),
                value / BigInteger.of(3),
                value / BigInteger.of(2),
                value * BigInteger.of(2) / BigInteger.of(3),
            )
            val b = (1..5).map { value - BigInteger.of(it) }.reversed().filter { it.signum() == 1 }
            (a + b).distinct().filterNot { it == value }.filter { it in range }
        }
    }
}

private class BigIntegerClassifier(private val min: BigInteger, private val max: BigInteger) : Classifier<BigInteger> {
    constructor(range: ClosedRange<BigInteger>) : this(range.start, range.endInclusive)

    override fun classify(value: BigInteger): String? = when {
        value == BigInteger.ZERO -> "ZERO"
        value == min -> "MIN"
        value == max -> "MAX"
        value.signum() == 1 && value.isEven() -> "POSITIVE EVEN"
        value.signum() == 1 -> "POSITIVE ODD"
        value.signum() == -1 && value.isEven() -> "NEGATIVE EVEN"
        value.signum() == -1 -> "NEGATIVE ODD"
        else -> null
    }
}

private val defaultRange = BigInteger.of(Int.MIN_VALUE)..BigInteger.of(Int.MAX_VALUE)

fun Arb.Companion.bigInt(range: ClosedRange<BigInteger> = defaultRange): Arb<BigInteger> {
    val edgeCases = listOf(
        range.start,
        BigInteger.of(-1),
        BigInteger.ZERO,
        BigInteger.of(1),
        range.endInclusive,
    ).filter { it in range }.distinct()
    return ArbitraryBuilder.create { randomBigInteger(it.random, range) }
        .withEdgecases(edgeCases)
        .withShrinker(BigIntegerShrinker(range))
        .withClassifier(BigIntegerClassifier(range))
        .build()
}

fun Arb.Companion.numericString(radix: Int = 10): Arb<String> {
    val validDigitList = (0..<radix).map { digit -> Character.forDigit(digit, radix).code }
    val validDigits = Arb.of(validDigitList).map(::Codepoint)
    val minBits = 32 * 20 + 1
    val minDigits = ceil(minBits / radix.toDouble()).toInt()
    val maxDigits = minDigits * 2
    return Arb.string(minSize = minDigits, maxSize = maxDigits, codepoints = validDigits)
        .filterNot { s -> s.startsWith("0") }
}
