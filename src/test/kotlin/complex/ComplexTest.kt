package garden.ephemeral.calculator.complex

import garden.ephemeral.calculator.creals.PrecisionOverflowError
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.shouldBeCloseTo
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ComplexTest {
    @Test
    fun testToString() {
        assertSoftly {
            Complex(Real.valueOf(3), Real.valueOf(2)).toString() shouldBe "3.0000000000 + 2.0000000000i"
            Complex(Real.valueOf(3), Real.valueOf(-2)).toString() shouldBe "3.0000000000 - 2.0000000000i"
            Complex(Real.valueOf(-3), Real.valueOf(2)).toString() shouldBe "-3.0000000000 + 2.0000000000i"
            Complex(Real.valueOf(-3), Real.valueOf(-2)).toString() shouldBe "-3.0000000000 - 2.0000000000i"
            Complex(Real.valueOf(0), Real.valueOf(0)).toString() shouldBe "0.0000000000"
            Complex(Real.valueOf(3), Real.valueOf(0)).toString() shouldBe "3.0000000000"
            Complex(Real.valueOf(-3), Real.valueOf(0)).toString() shouldBe "-3.0000000000"
            Complex(Real.valueOf(0), Real.valueOf(2)).toString() shouldBe "2.0000000000i"
            Complex(Real.valueOf(0), Real.valueOf(-2)).toString() shouldBe "-2.0000000000i"
        }
    }

    @Test
    fun testPlus_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(1))
        val c = Complex(Real.valueOf(4), Real.valueOf(3))
        (a + b) shouldBeCloseTo c
    }

    @Test
    fun testPlus_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(1)
        val c = Complex(Real.valueOf(4), Real.valueOf(2))
        (a + b) shouldBeCloseTo c
    }

    @Test
    fun testMinus_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(1))
        val c = Complex(Real.valueOf(2), Real.valueOf(1))
        (a - b) shouldBeCloseTo c
    }

    @Test
    fun testMinus_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(1)
        val c = Complex(Real.valueOf(2), Real.valueOf(2))
        (a - b) shouldBeCloseTo c
    }

    @Test
    fun testTimes_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(2))
        val c = Complex(Real.valueOf(-1), Real.valueOf(8))
        (a * b) shouldBeCloseTo c
    }

    @Test
    fun testTimes_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(2)
        val c = Complex(Real.valueOf(6), Real.valueOf(4))
        (a * b) shouldBeCloseTo c
    }

    @Test
    fun testDiv_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(2))
        val c = Complex(Real.valueOf("1.4"), Real.valueOf("-0.8"))
        (a / b) shouldBeCloseTo c
    }

    @Test
    fun testDiv_Complex_ByZero() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        shouldThrow<PrecisionOverflowError> {
            (a / Complex.ZERO).toString()
        }
    }

    @Test
    fun testDiv_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(2)
        val c = Complex(Real.valueOf(1.5), Real.valueOf(1))
        (a / b) shouldBeCloseTo c
    }

    @Test
    fun testDiv_Real_ByZero() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))

        shouldThrow<PrecisionOverflowError> {
            (a / Real.ZERO).toString()
        }
    }

    @Test
    fun testConjugate() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(3), Real.valueOf(-2))
        a.conjugate shouldBeCloseTo b
    }

    @Test
    fun testNorm() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        a.norm shouldBeCloseTo "3.60555127546398929312"
    }

    @Test
    fun testSquaredNorm() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        a.squaredNorm shouldBeCloseTo "13.00000000000000000000"
    }

    @Test
    fun testArgument_TopRightQuadrant() {
        Complex(Real.valueOf(3), Real.valueOf(2)).argument shouldBeCloseTo "0.58800260354756755125"
    }

    @Test
    fun testArgument_TopLeftQuadrant() {
        Complex(Real.valueOf(-3), Real.valueOf(2)).argument shouldBeCloseTo "2.55359005004222568722"
    }

    @Test
    fun testArgument_BottomLeftQuadrant() {
        Complex(Real.valueOf(-3), Real.valueOf(-2)).argument shouldBeCloseTo "-2.55359005004222568722"
    }

    @Test
    fun testArgument_BottomRightQuadrant() {
        Complex(Real.valueOf(3), Real.valueOf(-2)).argument shouldBeCloseTo "-0.58800260354756755125"
    }

    @Test
    fun testArgument_PlusReal() {
        Complex(Real.valueOf(3), Real.ZERO).argument shouldBeCloseTo "0.00000000000000000000"
    }

    @Test
    fun testArgument_MinusReal() {
        Complex(Real.valueOf(-3), Real.ZERO).argument shouldBeCloseTo "3.14159265358979323846"
    }

    @Test
    fun testArgument_PlusImag() {
        Complex(Real.ZERO, Real.valueOf(2)).argument shouldBeCloseTo "1.57079632679489661923"
    }

    @Test
    fun testArgument_MinusImag() {
        Complex(Real.ZERO, Real.valueOf(-2)).argument shouldBeCloseTo "-1.57079632679489661923"
    }

    @Test
    fun testArgument_Zero() {
        Complex.ZERO.argument shouldBeCloseTo "0.00000000000000000000"
    }

    @Test
    fun testUnaryMinus() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(-3), Real.valueOf(-2))
        (-a) shouldBeCloseTo b
    }

    @Test
    fun testUnaryPlus() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        (+a) shouldBeCloseTo a
    }

    @Test
    fun testPow_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(2), Real.valueOf(1))
        val c = Complex(Real.valueOf("-5.60043034583960297990"), Real.valueOf("4.55775731029292673098"))
        a.pow(b) shouldBeCloseTo c
    }

    @Test
    fun testPow_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(2)
        val c = Complex(Real.valueOf(5), Real.valueOf(12))
        a.pow(b) shouldBeCloseTo c
    }
}
