package garden.ephemeral.calculator.creals

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isCloseTo
import garden.ephemeral.calculator.creals.util.times
import org.junit.jupiter.api.Test
import kotlin.math.exp

class RealFunctionsTest {

    @Test
    fun testMax() = assertThat(max(ONE, TWO)).isCloseTo("2.00000000000000000000")

    @Test
    fun testMin() = assertThat(min(ONE, TWO)).isCloseTo("1.00000000000000000000")

    @Test
    fun testAbs_One() = assertThat(abs(ONE)).isCloseTo("1.00000000000000000000")

    @Test
    fun testAbs_MinusOne() = assertThat(abs(MINUS_ONE)).isCloseTo("1.00000000000000000000")

    @Test
    fun testExp_Zero() = assertThat(exp(ZERO)).isCloseTo("1.00000000000000000000")

    @Test
    fun testExp_One() = assertThat(exp(ONE)).isCloseTo("2.71828182845904523536")

    @Test
    fun testLn_E() = assertThat(ln(exp(ONE))).isCloseTo("1.00000000000000000000")

    @Test
    fun testSqrt() {
        val sqrt13 = sqrt(THIRTEEN)
        assertThat(sqrt13 * sqrt13).isCloseTo("13.00000000000000000000")
    }

    @Test
    fun testSin() = assertThat(sin(Real.HALF_PI)).isCloseTo("1.00000000000000000000")

    @Test
    fun testAsin_One() = assertThat(asin(ONE)).isCloseTo("1.57079632679489661923")

    @Test
    fun testAsin_MinusOne() = assertThat(asin(-ONE)).isCloseTo("-1.57079632679489661923")

    @Test
    fun testAsin_Zero() = assertThat(asin(ZERO)).isCloseTo("0.00000000000000000000")

    @Test
    fun testAsin_SinOneHalf() = assertThat(asin(sin(ONE_HALF))).isCloseTo("0.50000000000000000000")

    @Test
    fun testAsin_SinOne() = assertThat(asin(sin(ONE))).isCloseTo("1.00000000000000000000")

    @Test
    fun testAcos() = assertThat(acos(cos(ONE))).isCloseTo("1.00000000000000000000")

    @Test
    fun testAtan_TanOne() = assertThat(atan(tan(ONE))).isCloseTo("1.00000000000000000000")

    @Test
    fun testATan_TanMinusOne() = assertThat(atan(tan(-ONE))).isCloseTo("-1.00000000000000000000")

    @Test
    fun testTan_AtanHuge() {
        val million = 1000_000.toBigInteger()
        val thousand = 1000.toBigInteger()
        val huge = Real.valueOf(million * million * thousand)
        assertThat(tan(atan(huge))).isCloseTo("1000000000000000.00000000000000000000")
    }

    @Test
    fun testAtan2_Zero() = assertThat(atan2(ZERO, ZERO)).isCloseTo("0.00000000000000000000")

    @Test
    fun testAtan2_PosX_ZeroY() = assertThat(atan2(ZERO, TWO)).isCloseTo("0.00000000000000000000")

    @Test
    fun testAtan2_PosX_PosY() = assertThat(atan2(ONE, TWO)).isCloseTo("0.46364760900080611621")

    @Test
    fun testAtan2_ZeroX_PosY() = assertThat(atan2(ONE, ZERO)).isCloseTo("1.57079632679489661923")

    @Test
    fun testAtan2_NegX_PosY() = assertThat(atan2(ONE, MINUS_TWO)).isCloseTo("2.67794504458898712225")

    @Test
    fun testAtan2_NegX_ZeroY() = assertThat(atan2(ZERO, MINUS_TWO)).isCloseTo("3.14159265358979323846")

    @Test
    fun testAtan2_NegX_NegY() = assertThat(atan2(MINUS_ONE, MINUS_TWO)).isCloseTo("-2.67794504458898712225")

    @Test
    fun testAtan2_ZeroX_NegY() = assertThat(atan2(MINUS_ONE, ZERO)).isCloseTo("-1.57079632679489661923")

    @Test
    fun testAtan2_PosX_NegY() = assertThat(atan2(MINUS_ONE, TWO)).isCloseTo("-0.46364760900080611621")

    @Test
    fun testSomeRandomValuesAgainstKotlinMath() {
        val epsilon = 0.000001
        var n = -10.0
        while (n < 10.0) {
            assertThat(kotlin.math.sin(n)).isCloseTo(sin(Real.valueOf(n)).toDouble(), epsilon)
            assertThat(kotlin.math.cos(n)).isCloseTo(cos(Real.valueOf(n)).toDouble(), epsilon)
            assertThat(exp(n)).isCloseTo(exp(Real.valueOf(n)).toDouble(), epsilon)
            if (n > 0.0) {
                assertThat(exp(n)).isCloseTo(exp(Real.valueOf(n)).toDouble(), epsilon)
            }
            n += 2.0
        }
        assertThat(kotlin.math.cos(12345678.0)).isCloseTo(cos(Real.valueOf(12345678)).toDouble(), epsilon)
    }

    @Test
    fun testSinh() = assertAll {
        assertThat(sinh(ZERO)).isCloseTo("0.00000000000000000000")
        assertThat(sinh(ln(TWO))).isCloseTo("0.75000000000000000000")
        assertThat(sinh(-ln(TWO))).isCloseTo("-0.75000000000000000000")
        assertThat(sinh(ln(FOUR))).isCloseTo("1.87500000000000000000")
        assertThat(sinh(-ln(FOUR))).isCloseTo("-1.87500000000000000000")
    }

    @Test
    fun testCosh() = assertAll {
        assertThat(cosh(ZERO)).isCloseTo("1.00000000000000000000")
        assertThat(cosh(ln(TWO))).isCloseTo("1.25000000000000000000")
        assertThat(cosh(-ln(TWO))).isCloseTo("1.25000000000000000000")
        assertThat(cosh(ln(FOUR))).isCloseTo("2.12500000000000000000")
        assertThat(cosh(-ln(FOUR))).isCloseTo("2.12500000000000000000")
    }

    @Test
    fun testTanh() = assertAll {
        assertThat(tanh(ZERO)).isCloseTo("0.00000000000000000000")
        assertThat(tanh(ln(TWO))).isCloseTo("0.60000000000000000000")
        assertThat(tanh(-ln(TWO))).isCloseTo("-0.60000000000000000000")
        assertThat(tanh(ln(FOUR))).isCloseTo("0.88235294117647058823")
        assertThat(tanh(-ln(FOUR))).isCloseTo("-0.88235294117647058823")
    }

    @Test
    fun testAsinh() = assertAll {
        assertThat(asinh(ZERO)).isCloseTo("0.00000000000000000000")
        assertThat(asinh(TWO)).isCloseTo("1.44363547517881034249")
        assertThat(asinh(-TWO)).isCloseTo("-1.44363547517881034249")
        assertThat(asinh(FOUR)).isCloseTo("2.09471254726110129425")
        assertThat(asinh(-FOUR)).isCloseTo("-2.09471254726110129425")
    }

    @Test
    fun testAcosh() = assertAll {
        assertThat(acosh(ONE)).isCloseTo("0.00000000000000000000")
        assertThat(acosh(TWO)).isCloseTo("1.31695789692481670862")
        assertThat(acosh(FOUR)).isCloseTo("2.06343706889556054673")
    }

    @Test
    fun testAtanh() = assertAll {
        assertThat(atanh(ZERO)).isCloseTo("0.00000000000000000000")
        assertThat(atanh(TWO.reciprocal())).isCloseTo("0.54930614433405484570")
        assertThat(atanh(FOUR.reciprocal())).isCloseTo("0.25541281188299534160")
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
