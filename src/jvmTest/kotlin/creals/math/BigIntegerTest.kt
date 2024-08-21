package garden.ephemeral.calculator.creals.math

import garden.ephemeral.calculator.util.row
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldHaveSameHashCodeAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll
import org.junit.jupiter.api.assertThrows

class BigIntegerTest : FreeSpec({

    "creating from int" {
        BigInteger.of(1) shouldBe BigInteger.ONE
    }

    "creating from long" {
        BigInteger.of(1L) shouldBe BigInteger.ONE
    }

    "creating from string" {
        BigInteger.of("1") shouldBe BigInteger.ONE
        BigInteger.of("123") shouldBe BigInteger.of(123)
        BigInteger.of("-456") shouldBe BigInteger.of(-456)
        BigInteger.of("1_234") shouldBe BigInteger.of(1234)
        BigInteger.of("+5_678") shouldBe BigInteger.of(5678)
        assertThrows<IllegalArgumentException> {
            BigInteger.of("")
        }
        assertThrows<IllegalArgumentException> {
            BigInteger.of("@#$%#$%")
        }
    }

    val additionExamples = listOf(
        row(0L, 0L, 0L),
        row(0L, 4L, 4L),
        row(4L, 0L, 4L),
        row(5L, 2L, 7L),
        row(2L, 5L, 7L),
        row(2L, -5L, -3L),
        row(-2L, 5L, 3L),
        row(6L, -6L, 0L),
        row(-6L, 6L, 0L),
        row(0xFFFF_FFF0L, 0xFL, 0xFFFF_FFFFL),
        row(0xFFFF_FFF0L, 0x12L, 0x1_0000_0002L),
        row(-0xFFFF_FFF0L, -0xFL, -0xFFFF_FFFFL),
        row(-0xFFFF_FFF0L, -0x12L, -0x1_0000_0002L),
    )

    "plus" - {
        withData(additionExamples) { (aLong, bLong, cLong) ->
            val addend1 = BigInteger.of(aLong)
            val addend2 = BigInteger.of(bLong)
            val expectedSum = BigInteger.of(cLong)
            (addend1 + addend2) shouldBe expectedSum
        }
    }

    "minus" - {
        withData(additionExamples) { (aLong, bLong, cLong) ->
            val minuend = BigInteger.of(cLong)
            val subtrahend = BigInteger.of(aLong)
            val expectedDifference = BigInteger.of(bLong)
            (minuend - subtrahend) shouldBe expectedDifference
        }
    }

    val multiplicationByZeroExamples = listOf(
        row(0L, 0L, 0L),
        row(0L, 4L, 0L),
        row(4L, 0L, 0L),
    )
    val multiplicationExamples = listOf(
        row(3L, 2L, 6L),
        row(3L, -2L, -6L),
        row(2L, -3L, -6L),
        row(-3L, 2L, -6L),
        row(-2L, 3L, -6L),
        row(-3L, -2L, 6L),
        row(0xFFFF_FFF0L, 2L, 0x1_FFFF_FFE0L),
        row(-0xFFFF_FFF0L, 2L, -0x1_FFFF_FFE0L),
    )
    val largeMultiplicationExamples = listOf(
        row("2882382797", "2882382797", "8308130588441543209"),
        row("2882382797", "8308130588441543209", "23947212683353391185753775573"),
        row("8308130588441543209", "8308130588441543209", "69025033874598023025428114189414017681"),
        row("2882382797", "23947212683353391185753775573", "69025033874598023025428114189414017681"),
    )

    "times" - {
        withData(multiplicationByZeroExamples + multiplicationExamples) { (aLong, bLong, cLong) ->
            val factor1 = BigInteger.of(aLong)
            val factor2 = BigInteger.of(bLong)
            val expectedProduct = BigInteger.of(cLong)
            (factor1 * factor2) shouldBe expectedProduct
            (factor2 * factor1) shouldBe expectedProduct
        }
        withData(largeMultiplicationExamples) { (aString, bString, cString) ->
            val factor1 = BigInteger.of(aString)
            val factor2 = BigInteger.of(bString)
            val expectedProduct = BigInteger.of(cString)
            (factor1 * factor2) shouldBe expectedProduct
            (factor2 * factor1) shouldBe expectedProduct
        }
    }

    val divisionWithRemainderExamples = listOf(
        row(5L, 2L, 2L, 1L),
        row(15L, 2L, 7L, 1L),
        row(15L, 7L, 2L, 1L),
        row(15L, -7L, -2L, 1L),
        row(-15L, 7L, -2L, -1L),
        row(-15L, -7L, 2L, -1L),
    )
    val divisionByZeroExamples = listOf(
        row(0L),
        row(3L),
        row(-5L),
    )

    "div" - {
        withData(multiplicationExamples) { (aLong, bLong, cLong) ->
            val dividend = BigInteger.of(cLong)
            val divisor = BigInteger.of(aLong)
            val expectedQuotient = BigInteger.of(bLong)
            (dividend / divisor) shouldBe expectedQuotient
        }
        withData(largeMultiplicationExamples) { (aString, bString, cString) ->
            val divisor = BigInteger.of(aString)
            val dividend = BigInteger.of(cString)
            val expectedQuotient = BigInteger.of(bString)
            (dividend / divisor) shouldBe expectedQuotient
        }
        withData(divisionWithRemainderExamples) { (aLong, bLong, cLong, _) ->
            val dividend = BigInteger.of(aLong)
            val divisor = BigInteger.of(bLong)
            val expectedQuotient = BigInteger.of(cLong)
            (dividend / divisor) shouldBe expectedQuotient
        }
        withData(divisionByZeroExamples) { (aLong) ->
            val dividend = BigInteger.of(aLong)
            assertThrows<ArithmeticException> {
                dividend / BigInteger.ZERO
            }
        }
    }

    "rem" - {
        withData(divisionWithRemainderExamples) { (aLong, bLong, _, dLong) ->
            val dividend = BigInteger.of(aLong)
            val divisor = BigInteger.of(bLong)
            val expectedRemainder = BigInteger.of(dLong)
            (dividend % divisor) shouldBe expectedRemainder
        }
        withData(divisionByZeroExamples) { (aLong) ->
            val dividend = BigInteger.of(aLong)
            assertThrows<ArithmeticException> {
                dividend / BigInteger.ZERO
            }
        }
    }

    "unaryPlus" {
        val a = BigInteger.of(123)
        +a shouldBe a
    }

    "unaryMinus" {
        val a = BigInteger.of(123)
        val minusA = BigInteger.of(-123)
        -a shouldBe minusA
    }

    "inc" {
        val a = BigInteger.of(123)
        val b = BigInteger.of(124)
        var v = a
        v++
        v shouldBe b
    }

    "dec" {
        val a = BigInteger.of(123)
        val b = BigInteger.of(122)
        var v = a
        v--
        v shouldBe b
    }

    val shiftLeftExamples = listOf(
        row(674L, 0, 674L),
        row(674L, -1, 337L),
        row(674L, 1, 1_348L),
        row(674L, 2, 2_696L),
        row(674L, 3, 5_392L),
        row(674L, 9, 345_088L),

        // just short of going into the next word
        row(674L, 22, 2_826_960_896L),
        // one bit into the next word
        row(674L, 23, 5_653_921_792L),

        // at least one test which would have to shift more than one whole word
        row(674L, 39, 370_535_418_560_512L),
    )

    "shl" - {
        withData(shiftLeftExamples) { (aLong, n, expectedResultLong) ->
            val a = BigInteger.of(aLong)
            val expectedResult = BigInteger.of(expectedResultLong)
            (a shl n) shouldBe expectedResult
        }
    }

    "shr" - {
        withData(shiftLeftExamples) { (expectedResultLong, n, aLong) ->
            val a = BigInteger.of(aLong)
            val expectedResult = BigInteger.of(expectedResultLong)
            (a shr n) shouldBe expectedResult
        }

        // Large values just blow out memory, so we limit it.
        val limitedLong = Arb.long(min = -((1L shl 32) - 1), max = (1L shl 32) - 1)
        val limitedShift = Arb.positiveInt(max = 1_000_000)

        "shifting right a negative amount is the same as shifting left" {
            checkAll(limitedLong, limitedShift) { aLong, n ->
                val a = BigInteger.of(aLong)
                val aShiftedLeft = a shl n
                (a shr (-n)) shouldBe aShiftedLeft
            }
        }

        "shifting left a negative amount is the same as shifting right" {
            checkAll(limitedLong, limitedShift) { aLong, n ->
                val a = BigInteger.of(aLong)
                val aShiftedRight = a shr n
                (a shl (-n)) shouldBe aShiftedRight
            }
        }

        "shifting left and then right the same amount ends up where you started" {
            checkAll(limitedLong, limitedShift) { aLong, n ->
                val a = BigInteger.of(aLong)
                val aShiftedLeft = a shl n
                (aShiftedLeft shr n) shouldBe a
            }
        }
    }

    "equals and hashCode" {
        val a = BigInteger.of(123)
        val b = BigInteger.of(123)
        assertSoftly {
            a shouldNotBeSameInstanceAs b
            a shouldBeEqual b
            a shouldBeEqualComparingTo b
            a shouldHaveSameHashCodeAs b
        }
    }

    "toString" {
        val a = BigInteger.of(123)
        a.toString() shouldBe "BigInteger[sign=POSITIVE, words=[123]]"
    }
})
