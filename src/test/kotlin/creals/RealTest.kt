package garden.ephemeral.calculator.creals

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test

class RealTest {

    @Test
    fun testSignum_One() = assertThat(ONE.signum()).isEqualTo(1)

    @Test
    fun testSignum_MinusOne() = assertThat(MINUS_ONE.signum()).isEqualTo(-1)

    @Test
    fun testSignum_Zero() = assertThat(ZERO.signum(-100)).isEqualTo(0)

    @Test
    fun testCompareTo() = assertThat(ONE.compareTo(TWO, -10)).isEqualTo(-1)

    @Test
    fun testToString() = assertThat(TWO.toString(4)).isEqualTo("2.0000")

    @Test
    fun testShiftLeft() = assertThat(ONE.shiftLeft(1)).isCloseTo("2.00000000000000000000")

    @Test
    fun testShiftRight() = assertThat(TWO.shiftRight(1)).isCloseTo("1.00000000000000000000")

    @Test
    fun testPlus() = assertThat(ONE + ONE).isCloseTo("2.00000000000000000000")

    @Test
    fun testValueOf() = assertAll {
        assertThat(Real.valueOf(4)).isCloseTo("4.00000000000000000000")
        assertThat(Real.valueOf(3)).isCloseTo("3.00000000000000000000")
    }

    @Test
    fun testUnaryMinus() = assertThat(-ONE + TWO).isCloseTo("1.00000000000000000000")

    @Test
    fun testTimes() = assertThat(TWO * TWO).isCloseTo("4.00000000000000000000")

    @Test
    fun testDiv() = assertThat((ONE / FOUR).shiftLeft(4)).isCloseTo("4.00000000000000000000")

    @Test
    fun testDiv_Negative() = assertThat(TWO / -ONE).isCloseTo("-2.00000000000000000000")

    @Test
    fun testDiv_MorePrecision() = assertThat((ONE / THIRTEEN) * THIRTEEN).isCloseTo("1.00000000000000000000")

    @Test
    fun testDiv_ByZero() {
        // XXX: I would prefer (one / zero) to immediately throw some kind of divide by zero error.
        //      Problem is, comparing to zero is surprisingly difficult.
        assertThat {
            (ONE / ZERO).toString()
        }.isFailure().isInstanceOf(PrecisionOverflowError::class)
    }

    @Test
    fun testToInt() = assertThat(THIRTEEN.toInt()).isEqualTo(13)

    @Test
    fun testToLong() = assertThat(THIRTEEN.toLong()).isEqualTo(13L)

    @Test
    fun testToFloat() = assertThat(THIRTEEN.toFloat()).isCloseTo(13.0f, 0.00000000000000000001f)

    @Test
    fun testToDouble() = assertThat(THIRTEEN.toDouble()).isCloseTo(13.0, 0.00000000000000000001)

    @Test
    fun testSomeRoundTripOperations() {
        val tmp = Real.PI + exp(Real.valueOf(-123))
        val tmp2 = tmp - Real.PI
        assertThat(ln(tmp2).toInt()).isEqualTo(-123)
        assertThat(ln(tmp2).toLong()).isEqualTo(-123L)
        assertThat(ln(tmp2).toFloat().toDouble()).isEqualTo(-123.0)
        assertThat(ln(tmp2).toDouble()).isEqualTo(-123.0)
    }

    companion object {
        private val ZERO = Real.valueOf(0)
        private val ONE = Real.valueOf(1)
        private val MINUS_ONE = Real.valueOf(-1)
        private val TWO = Real.valueOf(2)
        private val FOUR = Real.valueOf(4)
        private val THIRTEEN = Real.valueOf(13)
    }
}
