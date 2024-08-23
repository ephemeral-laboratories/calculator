package garden.ephemeral.calculator.creals.math

import garden.ephemeral.calculator.util.row
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldHaveSameHashCodeAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.intArray
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.uInt
import io.kotest.property.arbitrary.uLong
import io.kotest.property.checkAll
import io.kotest.property.withAssumptions

@OptIn(ExperimentalUnsignedTypes::class)
class BigIntegerTest : FreeSpec({

    "creating from int" {
        checkAll(Arb.int()) { value ->
            val expectedValue = BigInteger.of(value.toString())
            BigInteger.of(value) shouldBe expectedValue
        }
    }

    "creating from unsigned int" {
        checkAll(Arb.uInt()) { value ->
            val expectedValue = BigInteger.of(value.toString())
            BigInteger.of(value) shouldBe expectedValue
        }
    }

    "creating from long" {
        checkAll(Arb.long()) { value ->
            val expectedValue = BigInteger.of(value.toString())
            BigInteger.of(value) shouldBe expectedValue
        }
    }

    "creating from unsigned long" {
        checkAll(Arb.uLong()) { value ->
            val expectedValue = BigInteger.of(value.toString())
            BigInteger.of(value) shouldBe expectedValue
        }
    }

    "creating from string" - {
        "valid examples" - {
            withData(
                row("1", 1),
                row("123", 123),
                row("-456", -456),
                row("1_234", 1234),
                row("+5_678", 5678),
            ) { (string, expectedInt) ->
                BigInteger.of(string) shouldBe BigInteger.of(expectedInt)
            }
        }
        "invalid examples" - {
            withData(
                row(""),
                row(" "),
                row("@#$%#$%"),
            ) { (string) ->
                shouldThrow<IllegalArgumentException> {
                    BigInteger.of(string)
                }
            }
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
        "specific examples" - {
            withData(additionExamples) { (aLong, bLong, cLong) ->
                val addend1 = BigInteger.of(aLong)
                val addend2 = BigInteger.of(bLong)
                val expectedSum = BigInteger.of(cLong)
                (addend1 + addend2) shouldBe expectedSum
            }
        }
        "commutative property" {
            checkAll(Arb.bigInt(), Arb.bigInt()) { a, b ->
                a + b shouldBe b + a
            }
        }
        "associative property" {
            checkAll(Arb.bigInt(), Arb.bigInt(), Arb.bigInt()) { a, b, c ->
                (a + b) + c shouldBe a + (b + c)
            }
        }
        "identity property" {
            checkAll(Arb.bigInt()) { a ->
                a + BigInteger.ZERO shouldBe a
            }
        }
    }

    "minus" - {
        "specific examples" - {
            withData(additionExamples) { (aLong, bLong, cLong) ->
                val minuend = BigInteger.of(cLong)
                val subtrahend = BigInteger.of(aLong)
                val expectedDifference = BigInteger.of(bLong)
                (minuend - subtrahend) shouldBe expectedDifference
            }
        }
        "ordering property" {
            checkAll(Arb.bigInt(), Arb.bigInt()) { a, b ->
                a - b shouldBe -(b - a)
            }
        }
        "identity property" {
            checkAll(Arb.bigInt()) { a ->
                a - BigInteger.ZERO shouldBe a
            }
        }
        "subtracting a number from itself" {
            checkAll(Arb.bigInt()) { a ->
                a - a shouldBe BigInteger.ZERO
            }
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
        "smaller examples" - {
            withData(multiplicationByZeroExamples + multiplicationExamples) { (aLong, bLong, cLong) ->
                val factor1 = BigInteger.of(aLong)
                val factor2 = BigInteger.of(bLong)
                val expectedProduct = BigInteger.of(cLong)
                (factor1 * factor2) shouldBe expectedProduct
                (factor2 * factor1) shouldBe expectedProduct
            }
        }
        "large examples" - {
            withData(largeMultiplicationExamples) { (aString, bString, cString) ->
                val factor1 = BigInteger.of(aString)
                val factor2 = BigInteger.of(bString)
                val expectedProduct = BigInteger.of(cString)
                (factor1 * factor2) shouldBe expectedProduct
                (factor2 * factor1) shouldBe expectedProduct
            }
        }
        "commutative property" {
            checkAll(Arb.bigInt(), Arb.bigInt()) { a, b ->
                a * b shouldBe b * a
            }
        }
        "associative property" {
            checkAll(Arb.bigInt(), Arb.bigInt(), Arb.bigInt()) { a, b, c ->
                (a * b) * c shouldBe a * (b * c)
            }
        }
        "distributive property" {
            checkAll(Arb.bigInt(), Arb.bigInt(), Arb.bigInt()) { a, b, c ->
                a * (b + c) shouldBe (a * b) + (a * c)
            }
        }
        "identity property" {
            checkAll(Arb.bigInt()) { a ->
                a * BigInteger.ONE shouldBe a
            }
        }
        "zero property" {
            checkAll(Arb.bigInt()) { a ->
                a * BigInteger.ZERO shouldBe BigInteger.ZERO
            }
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
        "specific examples" - {
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
        }
        "division by zero" - {
            withData(divisionByZeroExamples) { (aLong) ->
                val dividend = BigInteger.of(aLong)
                shouldThrow<ArithmeticException> {
                    dividend / BigInteger.ZERO
                }
            }
        }
        "identity property" {
            checkAll(Arb.bigInt()) { a ->
                a / BigInteger.ONE shouldBe a
            }
        }
        "dividing a number by itself" {
            checkAll(Arb.bigInt().filter { it.signum() != 0 }) { a ->
                a / a shouldBe BigInteger.ONE
            }
        }
    }

    "rem" - {
        "specific examples" - {
            withData(divisionWithRemainderExamples) { (aLong, bLong, _, dLong) ->
                val dividend = BigInteger.of(aLong)
                val divisor = BigInteger.of(bLong)
                val expectedRemainder = BigInteger.of(dLong)
                (dividend % divisor) shouldBe expectedRemainder
            }
        }
        "division by zero" - {
            withData(divisionByZeroExamples) { (aLong) ->
                val dividend = BigInteger.of(aLong)
                shouldThrow<ArithmeticException> {
                    dividend / BigInteger.ZERO
                }
            }
        }
    }

    "divRem" - {
        "specific examples" - {
            withData(divisionWithRemainderExamples) { (aLong, bLong, cLong, dLong) ->
                val dividend = BigInteger.of(aLong)
                val divisor = BigInteger.of(bLong)
                val expectedQuotient = BigInteger.of(cLong)
                val expectedRemainder = BigInteger.of(dLong)
                dividend.divRem(divisor) shouldBe BigInteger.QuotientWithRemainder(
                    quotient = expectedQuotient,
                    remainder = expectedRemainder,
                )
            }
        }
        "division by zero" - {
            withData(divisionByZeroExamples) { (aLong) ->
                val dividend = BigInteger.of(aLong)
                shouldThrow<ArithmeticException> {
                    dividend.divRem(BigInteger.ZERO)
                }
            }
        }
    }

    "unaryPlus" - {
        checkAll(Arb.bigInt()) { a ->
            +a shouldBe a
        }
    }

    "unaryMinus" - {
        withData(
            row(0, 0),
            row(123, -123),
            row(-456, 456),
        ) { (aInt, expectedInt) ->
            val a = BigInteger.of(aInt)
            val expected = BigInteger.of(expectedInt)
            -a shouldBe expected
        }
    }

    "inc" - {
        withData(
            row(0, 1),
            row(-1, 0),
            row(123, 124),
            row(-456, -455),
        ) { (aInt, expectedInt) ->
            val a = BigInteger.of(aInt)
            val expected = BigInteger.of(expectedInt)
            var v = a
            v++
            v shouldBe expected
        }
    }

    "dec" - {
        withData(
            row(0, -1),
            row(1, 0),
            row(123, 122),
            row(-456, -457),
        ) { (aInt, expectedInt) ->
            val a = BigInteger.of(aInt)
            val expected = BigInteger.of(expectedInt)
            var v = a
            v--
            v shouldBe expected
        }
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

    val shiftRightTruncatingExamples = listOf(
        row(674L, 2, 168L),
        row(674L, 3, 84L),
        row(674L, 4, 42L),
        row(674L, 5, 21L),
        row(674L, 6, 10L),
        row(674L, 7, 5L),
        row(674L, 8, 2L),
        row(674L, 9, 1L),
        row(674L, 10, 0L),
        row(674L, 11, 0L),
        row(674L, 31337, 0L),
    )

    "shr" - {
        withData(shiftLeftExamples) { (expectedResultLong, n, aLong) ->
            val a = BigInteger.of(aLong)
            val expectedResult = BigInteger.of(expectedResultLong)
            (a shr n) shouldBe expectedResult
        }
        withData(shiftRightTruncatingExamples) { (aLong, n, expectedResultLong) ->
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

    "and" {
        checkAll(
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
        ) { aArray, bArray ->
            val a = BigInteger.IntArrayHelpers.bigIntFromArray(aArray)
            val b = BigInteger.IntArrayHelpers.bigIntFromArray(bArray)

            val expected = BigInteger.IntArrayHelpers.bigIntFromArray(
                IntArray(aArray.size) { i -> aArray[i] and bArray[i] }
            )

            a and b shouldBe expected
        }
    }

    "or" {
        checkAll(
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
        ) { aArray, bArray ->
            val a = BigInteger.IntArrayHelpers.bigIntFromArray(aArray)
            val b = BigInteger.IntArrayHelpers.bigIntFromArray(bArray)

            val expected = BigInteger.IntArrayHelpers.bigIntFromArray(
                IntArray(aArray.size) { i -> aArray[i] or bArray[i] }
            )

            a or b shouldBe expected
        }
    }

    "xor" {
        checkAll(
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
        ) { aArray, bArray ->
            val a = BigInteger.IntArrayHelpers.bigIntFromArray(aArray)
            val b = BigInteger.IntArrayHelpers.bigIntFromArray(bArray)

            val expected = BigInteger.IntArrayHelpers.bigIntFromArray(
                IntArray(aArray.size) { i -> aArray[i] xor bArray[i] }
            )

            a xor b shouldBe expected
        }
    }

    "andNot" {
        checkAll(
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
        ) { aArray, bArray ->
            val a = BigInteger.IntArrayHelpers.bigIntFromArray(aArray)
            val b = BigInteger.IntArrayHelpers.bigIntFromArray(bArray)

            val expected = BigInteger.IntArrayHelpers.bigIntFromArray(
                IntArray(aArray.size) { i -> aArray[i] and bArray[i].inv() }
            )

            a andNot b shouldBe expected
        }
    }

    "not" {
        checkAll(
            Arb.intArray(length = Arb.int(3..3), content = Arb.int()),
        ) { aArray ->
            val a = BigInteger.IntArrayHelpers.bigIntFromArray(aArray)

            val expected = BigInteger.IntArrayHelpers.bigIntFromArray(
                IntArray(aArray.size) { i -> aArray[i].inv() }
            )

            a.not() shouldBe expected
        }
    }

    "equals and hashCode" {
        checkAll(Arb.int()) { aInt ->
            val a = BigInteger.of(aInt)
            val b = BigInteger.of(aInt)
            withAssumptions(aInt in -16..16) {
                a shouldBeSameInstanceAs b
            }
            withAssumptions(aInt !in -16..16) {
                assertSoftly {
                    a shouldNotBeSameInstanceAs b
                    a shouldBeEqual b
                    a shouldBeEqualComparingTo b
                    a shouldHaveSameHashCodeAs b
                }
            }
        }
    }

    "toString" - {
        "for values which fit in int" {
            checkAll(Arb.int()) { aInt ->
                val a = BigInteger.of(aInt)
                a.toString() shouldBe aInt.toString()
            }
        }
        "for larger values" {
            checkAll(Arb.numericString()) { string ->
                val a = BigInteger.of(string)
                a.toString() shouldBe string
            }
        }
    }

    "toString with radix" - {
        "for values which fit in int" - {
            withData(Character.MIN_RADIX..Character.MAX_RADIX) { radix ->
                checkAll(Arb.int()) { aInt ->
                    val a = BigInteger.of(aInt)
                    a.toString(radix = radix) shouldBe aInt.toString(radix = radix)
                }
            }
        }
        "for larger values" - {
            withData(Character.MIN_RADIX..Character.MAX_RADIX) { radix ->
                checkAll(Arb.numericString(radix)) { string ->
                    val a = BigInteger.of(value = string, radix = radix)
                    a.toString(radix = radix) shouldBe string
                }
            }
        }
        "for invalid radix" {
            shouldThrow<IllegalArgumentException> {
                BigInteger.ZERO.toString(radix = 1)
            }
            shouldThrow<IllegalArgumentException> {
                BigInteger.ZERO.toString(radix = 37)
            }
        }
    }
})
