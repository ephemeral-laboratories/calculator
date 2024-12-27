package garden.ephemeral.calculator.creals

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.gciatto.kt.math.BigInteger
import kotlin.math.exp

class RealFunctionsTest : FreeSpec({
    val zero = Real.valueOf(0)
    val one = Real.valueOf(1)
    val minusOne = Real.valueOf(-1)
    val two = Real.valueOf(2)
    val minusTwo = Real.valueOf(-2)
    val four = Real.valueOf(4)
    val thirteen = Real.valueOf(13)
    val oneHalf = one / two

    "max" {
        max(one, two) shouldBeCloseTo "2.00000000000000000000"
    }

    "min" {
        min(one, two) shouldBeCloseTo "1.00000000000000000000"
    }

    "abs" - {
        "0" {
            abs(zero) shouldBeCloseTo "0.00000000000000000000"
        }
        "1" {
            abs(one) shouldBeCloseTo "1.00000000000000000000"
        }
        "-1" {
            abs(minusOne) shouldBeCloseTo "1.00000000000000000000"
        }
    }

    "exp" - {
        "0" {
            exp(zero) shouldBeCloseTo "1.00000000000000000000"
        }
        "1" {
            exp(one) shouldBeCloseTo "2.71828182845904523536"
        }
    }

    "ln" - {
        "1" {
            ln(one) shouldBeCloseTo "0.00000000000000000000"
        }
        "e" {
            ln(exp(one)) shouldBeCloseTo "1.00000000000000000000"
        }
    }

    "sqrt" {
        val sqrt13 = sqrt(thirteen)
        (sqrt13 * sqrt13) shouldBeCloseTo "13.00000000000000000000"
    }

    "sin" {
        sin(Real.HALF_PI) shouldBeCloseTo "1.00000000000000000000"
    }

    "asin" - {
        "1" {
            asin(one) shouldBeCloseTo "1.57079632679489661923"
        }
        "-1" {
            asin(-one) shouldBeCloseTo "-1.57079632679489661923"
        }
        "0" {
            asin(zero) shouldBeCloseTo "0.00000000000000000000"
        }
        "sin 1/2" {
            asin(sin(oneHalf)) shouldBeCloseTo "0.50000000000000000000"
        }
        "sin 1" {
            asin(sin(one)) shouldBeCloseTo "1.00000000000000000000"
        }
    }

    "acos" {
        acos(cos(one)) shouldBeCloseTo "1.00000000000000000000"
    }

    "atan" - {
        "tan 1" {
            atan(tan(one)) shouldBeCloseTo "1.00000000000000000000"
        }
        "tan -1" {
            atan(tan(-one)) shouldBeCloseTo "-1.00000000000000000000"
        }
    }

    "atan2" - {
        "0" {
            atan2(zero, zero) shouldBeCloseTo "0.00000000000000000000"
        }
        "pos X" {
            atan2(zero, two) shouldBeCloseTo "0.00000000000000000000"
        }
        "pos X pos Y" {
            atan2(one, two) shouldBeCloseTo "0.46364760900080611621"
        }
        "pos Y" {
            atan2(one, zero) shouldBeCloseTo "1.57079632679489661923"
        }
        "neg X pos Y" {
            atan2(one, minusTwo) shouldBeCloseTo "2.67794504458898712225"
        }
        "neg X" {
            atan2(zero, minusTwo) shouldBeCloseTo "3.14159265358979323846"
        }
        "neg X neg Y" {
            atan2(minusOne, minusTwo).shouldBeCloseTo("-2.67794504458898712225")
        }
        "neg Y" {
            atan2(minusOne, zero).shouldBeCloseTo("-1.57079632679489661923")
        }
        "pos X neg Y" {
            atan2(minusOne, two).shouldBeCloseTo("-0.46364760900080611621")
        }
    }

    "tan for huge atan" {
        val million = BigInteger.of(1000_000)
        val thousand = BigInteger.of(1000)
        val huge = Real.valueOf(million * million * thousand)
        tan(atan(huge)) shouldBeCloseTo "1000000000000000.00000000000000000000"
    }

    "comparing some random values against kotlin math" {
        val epsilon = 0.000001
        var n = -10.0
        while (n < 10.0) {
            kotlin.math.sin(n) shouldBe (sin(Real.valueOf(n)).toDouble() plusOrMinus epsilon)
            kotlin.math.cos(n) shouldBe (cos(Real.valueOf(n)).toDouble() plusOrMinus epsilon)
            exp(n) shouldBe (exp(Real.valueOf(n)).toDouble() plusOrMinus epsilon)
            if (n > 0.0) {
                exp(n) shouldBe (exp(Real.valueOf(n)).toDouble() plusOrMinus epsilon)
            }
            n += 2.0
        }
        kotlin.math.cos(12345678.0) shouldBe (cos(Real.valueOf(12345678)).toDouble() plusOrMinus epsilon)
    }

    "sinh" {
        assertSoftly {
            sinh(zero) shouldBeCloseTo "0.00000000000000000000"
            sinh(ln(two)) shouldBeCloseTo "0.75000000000000000000"
            sinh(-ln(two)) shouldBeCloseTo "-0.75000000000000000000"
            sinh(ln(four)) shouldBeCloseTo "1.87500000000000000000"
            sinh(-ln(four)) shouldBeCloseTo "-1.87500000000000000000"
        }
    }

    "cosh" {
        assertSoftly {
            cosh(zero) shouldBeCloseTo "1.00000000000000000000"
            cosh(ln(two)) shouldBeCloseTo "1.25000000000000000000"
            cosh(-ln(two)) shouldBeCloseTo "1.25000000000000000000"
            cosh(ln(four)) shouldBeCloseTo "2.12500000000000000000"
            cosh(-ln(four)) shouldBeCloseTo "2.12500000000000000000"
        }
    }

    "tanh" {
        assertSoftly {
            tanh(zero) shouldBeCloseTo "0.00000000000000000000"
            tanh(ln(two)) shouldBeCloseTo "0.60000000000000000000"
            tanh(-ln(two)) shouldBeCloseTo "-0.60000000000000000000"
            tanh(ln(four)) shouldBeCloseTo "0.88235294117647058823"
            tanh(-ln(four)) shouldBeCloseTo "-0.88235294117647058823"
        }
    }

    "asinh" {
        assertSoftly {
            asinh(zero) shouldBeCloseTo "0.00000000000000000000"
            asinh(two) shouldBeCloseTo "1.44363547517881034249"
            asinh(-two) shouldBeCloseTo "-1.44363547517881034249"
            asinh(four) shouldBeCloseTo "2.09471254726110129425"
            asinh(-four) shouldBeCloseTo "-2.09471254726110129425"
        }
    }

    "acosh" {
        assertSoftly {
            acosh(one) shouldBeCloseTo "0.00000000000000000000"
            acosh(two) shouldBeCloseTo "1.31695789692481670862"
            acosh(four) shouldBeCloseTo "2.06343706889556054673"
        }
    }

    "atanh" {
        assertSoftly {
            atanh(zero) shouldBeCloseTo "0.00000000000000000000"
            atanh(two.reciprocal()) shouldBeCloseTo "0.54930614433405484570"
            atanh(four.reciprocal()) shouldBeCloseTo "0.25541281188299534160"
        }
    }
})
