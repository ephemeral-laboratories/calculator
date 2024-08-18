package garden.ephemeral.calculator.creals

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

class RealTest : FreeSpec({
    val zero = Real.valueOf(0)
    val one = Real.valueOf(1)
    val minusOne = Real.valueOf(-1)
    val two = Real.valueOf(2)
    val four = Real.valueOf(4)
    val thirteen = Real.valueOf(13)

    "signum" - {
        "for positive value" {
            one.signum() shouldBe 1
        }

        "for negative value" {
            minusOne.signum() shouldBe -1
        }

        "for zero" {
            zero.signum(-100) shouldBe 0
        }
    }

    "compareTo" {
        one.compareTo(two, -10) shouldBe -1
    }

    "toString" - {
        "0" {
            zero.toString(4) shouldBe "0.0000"
        }
        "positive" {
            two.toString(4) shouldBe "2.0000"
        }
        "negative" {
            minusOne.toString(4) shouldBe "-1.0000"
        }
    }

    "shiftLeft" {
        one.shiftLeft(1) shouldBeCloseTo "2.00000000000000000000"
    }

    "shiftRight" {
        two.shiftRight(1) shouldBeCloseTo "1.00000000000000000000"
    }

    "plus" {
        (one + one) shouldBeCloseTo "2.00000000000000000000"
    }

    "valueOf" {
        assertSoftly {
            Real.valueOf(4) shouldBeCloseTo "4.00000000000000000000"
            Real.valueOf(3) shouldBeCloseTo "3.00000000000000000000"
        }
    }

    "unary minus" {
        (-one + two) shouldBeCloseTo "1.00000000000000000000"
    }

    "unary plus" {
        (+one + two) shouldBeCloseTo "3.00000000000000000000"
    }

    "times" {
        (two * two) shouldBeCloseTo "4.00000000000000000000"
    }

    "div" - {
        "by positive value" {
            ((one / four).shiftLeft(4)) shouldBeCloseTo "4.00000000000000000000"
        }

        "by negative value" {
            (two / -one) shouldBeCloseTo "-2.00000000000000000000"
        }

        "by thirteen" {
            ((one / thirteen) * thirteen) shouldBeCloseTo "1.00000000000000000000"
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
