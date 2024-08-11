package garden.ephemeral.calculator.creals

import garden.ephemeral.calculator.creals.util.times
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.exp

class RealFunctionsTest {

    @Test
    fun testMax() {
        max(ONE, TWO) shouldBeCloseTo "2.00000000000000000000"
    }

    @Test
    fun testMin() {
        min(ONE, TWO) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testAbs_One() {
        abs(ONE) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testAbs_MinusOne() {
        abs(MINUS_ONE) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testExp_Zero() {
        exp(ZERO) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testExp_One() {
        exp(ONE) shouldBeCloseTo "2.71828182845904523536"
    }

    @Test
    fun testLn_E() {
        ln(exp(ONE)) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testSqrt() {
        val sqrt13 = sqrt(THIRTEEN)
        (sqrt13 * sqrt13) shouldBeCloseTo "13.00000000000000000000"
    }

    @Test
    fun testSin() {
        sin(Real.HALF_PI) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testAsin_One() {
        asin(ONE) shouldBeCloseTo "1.57079632679489661923"
    }

    @Test
    fun testAsin_MinusOne() {
        asin(-ONE) shouldBeCloseTo "-1.57079632679489661923"
    }

    @Test
    fun testAsin_Zero() {
        asin(ZERO) shouldBeCloseTo "0.00000000000000000000"
    }

    @Test
    fun testAsin_SinOneHalf() {
        asin(sin(ONE_HALF)) shouldBeCloseTo "0.50000000000000000000"
    }

    @Test
    fun testAsin_SinOne() {
        asin(sin(ONE)) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testAcos() {
        acos(cos(ONE)) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testAtan_TanOne() {
        atan(tan(ONE)) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testATan_TanMinusOne() {
        atan(tan(-ONE)) shouldBeCloseTo "-1.00000000000000000000"
    }

    @Test
    fun testTan_AtanHuge() {
        val million = 1000_000.toBigInteger()
        val thousand = 1000.toBigInteger()
        val huge = Real.valueOf(million * million * thousand)
        tan(atan(huge)) shouldBeCloseTo "1000000000000000.00000000000000000000"
    }

    @Test
    fun testAtan2_Zero() {
        atan2(ZERO, ZERO) shouldBeCloseTo "0.00000000000000000000"
    }

    @Test
    fun testAtan2_PosX_ZeroY() {
        atan2(ZERO, TWO) shouldBeCloseTo "0.00000000000000000000"
    }

    @Test
    fun testAtan2_PosX_PosY() {
        atan2(ONE, TWO) shouldBeCloseTo "0.46364760900080611621"
    }

    @Test
    fun testAtan2_ZeroX_PosY() {
        atan2(ONE, ZERO) shouldBeCloseTo "1.57079632679489661923"
    }

    @Test
    fun testAtan2_NegX_PosY() {
        atan2(ONE, MINUS_TWO) shouldBeCloseTo "2.67794504458898712225"
    }

    @Test
    fun testAtan2_NegX_ZeroY() {
        atan2(ZERO, MINUS_TWO) shouldBeCloseTo "3.14159265358979323846"
    }

    @Test
    fun testAtan2_NegX_NegY() {
        atan2(MINUS_ONE, MINUS_TWO).shouldBeCloseTo("-2.67794504458898712225")
    }

    @Test
    fun testAtan2_ZeroX_NegY() {
        atan2(MINUS_ONE, ZERO).shouldBeCloseTo("-1.57079632679489661923")
    }

    @Test
    fun testAtan2_PosX_NegY() {
        atan2(MINUS_ONE, TWO).shouldBeCloseTo("-0.46364760900080611621")
    }

    @Test
    fun testSomeRandomValuesAgainstKotlinMath() {
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

    @Test
    fun testSinh() {
        assertSoftly {
            sinh(ZERO) shouldBeCloseTo "0.00000000000000000000"
            sinh(ln(TWO)) shouldBeCloseTo "0.75000000000000000000"
            sinh(-ln(TWO)) shouldBeCloseTo "-0.75000000000000000000"
            sinh(ln(FOUR)) shouldBeCloseTo "1.87500000000000000000"
            sinh(-ln(FOUR)) shouldBeCloseTo "-1.87500000000000000000"
        }
    }

    @Test
    fun testCosh() {
        assertSoftly {
            cosh(ZERO) shouldBeCloseTo "1.00000000000000000000"
            cosh(ln(TWO)) shouldBeCloseTo "1.25000000000000000000"
            cosh(-ln(TWO)) shouldBeCloseTo "1.25000000000000000000"
            cosh(ln(FOUR)) shouldBeCloseTo "2.12500000000000000000"
            cosh(-ln(FOUR)) shouldBeCloseTo "2.12500000000000000000"
        }
    }

    @Test
    fun testTanh() {
        assertSoftly {
            tanh(ZERO) shouldBeCloseTo "0.00000000000000000000"
            tanh(ln(TWO)) shouldBeCloseTo "0.60000000000000000000"
            tanh(-ln(TWO)) shouldBeCloseTo "-0.60000000000000000000"
            tanh(ln(FOUR)) shouldBeCloseTo "0.88235294117647058823"
            tanh(-ln(FOUR)) shouldBeCloseTo "-0.88235294117647058823"
        }
    }

    @Test
    fun testAsinh() {
        assertSoftly {
            asinh(ZERO) shouldBeCloseTo "0.00000000000000000000"
            asinh(TWO) shouldBeCloseTo "1.44363547517881034249"
            asinh(-TWO) shouldBeCloseTo "-1.44363547517881034249"
            asinh(FOUR) shouldBeCloseTo "2.09471254726110129425"
            asinh(-FOUR) shouldBeCloseTo "-2.09471254726110129425"
        }
    }

    @Test
    fun testAcosh() {
        assertSoftly {
            acosh(ONE) shouldBeCloseTo "0.00000000000000000000"
            acosh(TWO) shouldBeCloseTo "1.31695789692481670862"
            acosh(FOUR) shouldBeCloseTo "2.06343706889556054673"
        }
    }

    @Test
    fun testAtanh() {
        assertSoftly {
            atanh(ZERO) shouldBeCloseTo "0.00000000000000000000"
            atanh(TWO.reciprocal()) shouldBeCloseTo "0.54930614433405484570"
            atanh(FOUR.reciprocal()) shouldBeCloseTo "0.25541281188299534160"
        }
    }

    companion object {
        private val ZERO = Real.valueOf(0)
        private val ONE = Real.valueOf(1)
        private val MINUS_ONE = Real.valueOf(-1)
        private val TWO = Real.valueOf(2)
        private val MINUS_TWO = Real.valueOf(-2)
        private val FOUR = Real.valueOf(4)
        private val THIRTEEN = Real.valueOf(13)
        private val ONE_HALF = ONE / TWO
    }
}
