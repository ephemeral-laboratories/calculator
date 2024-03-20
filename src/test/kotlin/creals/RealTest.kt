package garden.ephemeral.calculator.creals

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RealTest {

    @Test
    fun testSignum_One() {
        ONE.signum() shouldBe 1
    }

    @Test
    fun testSignum_MinusOne() {
        MINUS_ONE.signum() shouldBe -1
    }

    @Test
    fun testSignum_Zero() {
        ZERO.signum(-100) shouldBe 0
    }

    @Test
    fun testCompareTo() {
        ONE.compareTo(TWO, -10) shouldBe -1
    }

    @Test
    fun testToString() {
        TWO.toString(4) shouldBe "2.0000"
    }

    @Test
    fun testShiftLeft() {
        ONE.shiftLeft(1) shouldBeCloseTo "2.00000000000000000000"
    }

    @Test
    fun testShiftRight() {
        TWO.shiftRight(1) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testPlus() {
        (ONE + ONE) shouldBeCloseTo "2.00000000000000000000"
    }

    @Test
    fun testValueOf() {
        assertSoftly {
            Real.valueOf(4) shouldBeCloseTo "4.00000000000000000000"
            Real.valueOf(3) shouldBeCloseTo "3.00000000000000000000"
        }
    }

    @Test
    fun testUnaryMinus() {
        (-ONE + TWO) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testTimes() {
        (TWO * TWO) shouldBeCloseTo "4.00000000000000000000"
    }

    @Test
    fun testDiv() {
        ((ONE / FOUR).shiftLeft(4)) shouldBeCloseTo "4.00000000000000000000"
    }

    @Test
    fun testDiv_Negative() {
        (TWO / -ONE) shouldBeCloseTo "-2.00000000000000000000"
    }

    @Test
    fun testDiv_MorePrecision() {
        ((ONE / THIRTEEN) * THIRTEEN) shouldBeCloseTo "1.00000000000000000000"
    }

    @Test
    fun testDiv_ByZero() {
        // XXX: I would prefer (one / zero) to immediately throw some kind of divide by zero error.
        //      Problem is, comparing to zero is surprisingly difficult.
        shouldThrow<PrecisionOverflowError> {
            (ONE / ZERO).toString()
        }
    }

    @Test
    fun testToInt() {
        THIRTEEN.toInt() shouldBe 13
    }

    @Test
    fun testToLong() {
        THIRTEEN.toLong() shouldBe 13L
    }

    @Test
    fun testToFloat() {
        THIRTEEN.toFloat() shouldBe (13.0f plusOrMinus 0.00000000000000000001f)
    }

    @Test
    fun testToDouble() {
        THIRTEEN.toDouble() shouldBe (13.0 plusOrMinus 0.00000000000000000001)
    }

    @Test
    fun testSomeRoundTripOperations() {
        val tmp = Real.PI + exp(Real.valueOf(-123))
        val tmp2 = tmp - Real.PI
        ln(tmp2).toInt() shouldBe -123
        ln(tmp2).toLong() shouldBe -123L
        ln(tmp2).toFloat() shouldBe -123.0f
        ln(tmp2).toDouble() shouldBe -123.0
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
