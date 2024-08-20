package garden.ephemeral.calculator.creals

import garden.ephemeral.calculator.creals.util.StringFloatRep
import garden.ephemeral.calculator.util.row
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class RealTest : FreeSpec({
    val zero = Real.valueOf(0)
    val one = Real.valueOf(1)
    val two = Real.valueOf(2)
    val four = Real.valueOf(4)
    val thirteen = Real.valueOf(13)
    val minusOne = Real.valueOf(-1)
    val minusTwo = Real.valueOf(-2)
    val minusFour = Real.valueOf(-4)
    val oneHalf = Real.valueOf(0.5)

    "signum" - {
        withData(
            row(one, 1, "positive value 1"),
            row(two, 1, "positive value 2"),
            row(minusOne, -1, "negative value 1"),
            row(minusTwo, -1, "negative value 2"),
        ) { (value, expected, _) ->
            value.signum() shouldBe expected
        }
        "for zero when tolerance is specified" {
            zero.signum(-100) shouldBe 0
        }
        "for zero when tolerance is not specified" {
            shouldThrow<PrecisionOverflowError> {
                zero.signum()
            }
        }
    }

    "compareTo" - {
        withData(
            row(two, one, 1, "lesser value"),
            row(one, two, -1, "greater value"),
            row(one, one, 0, "equal value"),
        ) { (a, b, expected, _) ->
            a.compareTo(b, -10) shouldBe expected
        }
    }

    "toStringFloatRep" - {
        withData(
            row(
                zero,
                StringFloatRep(sign = 0, mantissaDigits = listOf(0), radix = 10, exponent = 0),
                "0 equal value",
            ),
            row(
                one,
                StringFloatRep(sign = 1, mantissaDigits = listOf(1, 0, 0, 0, 0), radix = 10, exponent = 1),
                "positive value 1",
            ),
            row(
                two,
                StringFloatRep(sign = 1, mantissaDigits = listOf(2, 0, 0, 0, 0), radix = 10, exponent = 1),
                "positive value 2",
            ),
            row(
                minusOne,
                StringFloatRep(sign = -1, mantissaDigits = listOf(1, 0, 0, 0, 0), radix = 10, exponent = 1),
                "negative value 1",
            ),
            row(
                minusTwo,
                StringFloatRep(sign = -1, mantissaDigits = listOf(2, 0, 0, 0, 0), radix = 10, exponent = 1),
                "negative value 2",
            ),
            row(
                Real.valueOf(12),
                StringFloatRep(sign = 1, mantissaDigits = listOf(1, 2, 0, 0, 0), radix = 10, exponent = 2),
                "positive value with 2 digits to left of decimal point",
            )
        ) { (value, expected, _) ->
            value.toStringFloatRep(pointsOfPrecision = 5, radix = 10, msdPrecision = 5) shouldBe expected
        }
        "passing invalid pointsOfPrecision" {
            assertThrows<ArithmeticException> {
                one.toStringFloatRep(pointsOfPrecision = 0, radix = 10, msdPrecision = 5)
            }
            assertThrows<ArithmeticException> {
                one.toStringFloatRep(pointsOfPrecision = -1, radix = 10, msdPrecision = 5)
            }
        }
        "passing an msdPrecision which overflows" {
            assertThrows<PrecisionOverflowError> {
                one.toStringFloatRep(pointsOfPrecision = 5, radix = 10, msdPrecision = 1_000_000_000)
            }
        }
    }

    "toString" - {
        withData(
            row(one, "1.0000", "positive value 1"),
            row(two, "2.0000", "positive value 2"),
            row(zero, "0.0000", "0 equal value"),
            row(minusOne, "-1.0000", "negative value 1"),
            row(minusTwo, "-2.0000", "negative value 2"),
        ) { (value, expected, _) ->
            value.toString(pointsOfPrecision = 4) shouldBe expected
        }
    }

    "valueOf" - {
        withData(
            row("4", "4.00000000000000000000"),
            row("3", "3.00000000000000000000"),
            row("0", "0.00000000000000000000"),
            row("-1", "-1.00000000000000000000"),
        ) { (intValue, expected) ->
            Real.valueOf(intValue) shouldBeCloseTo expected
        }
    }

    "shiftLeft" - {
        withData(
            row(zero, "0.00000000000000000000"),
            row(oneHalf, "1.00000000000000000000"),
            row(one, "2.00000000000000000000"),
        ) { (value, expected) ->
            value.shiftLeft(1) shouldBeCloseTo expected
        }
    }

    "shiftRight" - {
        withData(
            row(zero, "0.00000000000000000000"),
            row(one, "0.50000000000000000000"),
            row(two, "1.00000000000000000000"),
        ) { (value, expected) ->
            value.shiftRight(1) shouldBeCloseTo expected
        }
    }

    "plus" - {
        withData(
            row(one, one, "2.00000000000000000000"),
            row(minusOne, two, "1.00000000000000000000"),
            row(minusOne, one, "0.00000000000000000000"),
            row(minusTwo, one, "-1.00000000000000000000"),
        ) { (a, b, expected) ->
            (a + b) shouldBeCloseTo expected
        }
    }

    "minus" - {
        withData(
            row(two, one, "1.00000000000000000000"),
            row(one, one, "0.00000000000000000000"),
            row(one, two, "-1.00000000000000000000"),
        ) { (a, b, expected) ->
            (a - b) shouldBeCloseTo expected
        }
    }

    "unary minus" - {
        withData(
            row(one, "-1.00000000000000000000"),
            row(zero, "0.00000000000000000000"),
            row(minusOne, "1.00000000000000000000"),
        ) { (value, expected) ->
            (-value) shouldBeCloseTo expected
        }
    }

    "unary plus" - {
        withData(
            row(one, "1.00000000000000000000"),
            row(zero, "0.00000000000000000000"),
            row(minusOne, "-1.00000000000000000000"),
        ) { (value, expected) ->
            (+value) shouldBeCloseTo expected
        }
    }

    "times" - {
        withData(
            row(two, two, "4.00000000000000000000"),
            row(two, minusTwo, "-4.00000000000000000000"),
            row(minusTwo, two, "-4.00000000000000000000"),
            row(minusTwo, minusTwo, "4.00000000000000000000"),
        ) { (a, b, expected) ->
            (a * b) shouldBeCloseTo expected
        }
    }

    "div" - {
        withData(
            row(one, four, "0.25000000000000000000"),
            row(four, minusTwo, "-2.00000000000000000000"),
            row(minusFour, two, "-2.00000000000000000000"),
            row(minusFour, minusTwo, "2.00000000000000000000"),
            row(one, thirteen, "0.07692307692307692308"),
        ) { (a, b, expected) ->
            (a / b) shouldBeCloseTo expected
        }
        "by zero" {
            // XXX: I would prefer (one / zero) to immediately throw some kind of divide by zero error.
            //      Problem is, comparing to zero is surprisingly difficult.
            shouldThrow<PrecisionOverflowError> {
                (one / zero).toString()
            }
        }
    }

    "toInt" {
        thirteen.toInt() shouldBe 13
    }

    "toLong" {
        thirteen.toLong() shouldBe 13L
    }

    "toFloat" {
        thirteen.toFloat() shouldBe (13.0f plusOrMinus 0.00000000000000000001f)
    }

    "toDouble" {
        thirteen.toDouble() shouldBe (13.0 plusOrMinus 0.00000000000000000001)
    }

    "some round trip operations" {
        val tmp = Real.PI + exp(Real.valueOf(-123))
        val tmp2 = tmp - Real.PI
        ln(tmp2).toInt() shouldBe -123
        ln(tmp2).toLong() shouldBe -123L
        ln(tmp2).toFloat() shouldBe -123.0f
        ln(tmp2).toDouble() shouldBe -123.0
    }
})
