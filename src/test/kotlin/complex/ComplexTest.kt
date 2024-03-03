package garden.ephemeral.calculator.complex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import garden.ephemeral.calculator.creals.PrecisionOverflowError
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.isCloseTo
import org.junit.jupiter.api.Test

class ComplexTest {
    @Test
    fun testToString() {
        assertAll {
            assertThat(Complex(Real.valueOf(3), Real.valueOf(2)).toString()).isEqualTo("3.0000000000 + 2.0000000000i")
            assertThat(Complex(Real.valueOf(3), Real.valueOf(-2)).toString()).isEqualTo("3.0000000000 - 2.0000000000i")
            assertThat(Complex(Real.valueOf(-3), Real.valueOf(2)).toString()).isEqualTo("-3.0000000000 + 2.0000000000i")
            assertThat(Complex(Real.valueOf(-3), Real.valueOf(-2)).toString()).isEqualTo("-3.0000000000 - 2.0000000000i")
            assertThat(Complex(Real.valueOf(0), Real.valueOf(0)).toString()).isEqualTo("0.0000000000")
            assertThat(Complex(Real.valueOf(3), Real.valueOf(0)).toString()).isEqualTo("3.0000000000")
            assertThat(Complex(Real.valueOf(-3), Real.valueOf(0)).toString()).isEqualTo("-3.0000000000")
            assertThat(Complex(Real.valueOf(0), Real.valueOf(2)).toString()).isEqualTo("2.0000000000i")
            assertThat(Complex(Real.valueOf(0), Real.valueOf(-2)).toString()).isEqualTo("-2.0000000000i")
        }
    }

    @Test
    fun testPlus_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(1))
        val c = Complex(Real.valueOf(4), Real.valueOf(3))
        assertThat(a + b).isCloseTo(c)
    }

    @Test
    fun testPlus_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(1)
        val c = Complex(Real.valueOf(4), Real.valueOf(2))
        assertThat(a + b).isCloseTo(c)
    }

    @Test
    fun testMinus_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(1))
        val c = Complex(Real.valueOf(2), Real.valueOf(1))
        assertThat(a - b).isCloseTo(c)
    }

    @Test
    fun testMinus_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(1)
        val c = Complex(Real.valueOf(2), Real.valueOf(2))
        assertThat(a - b).isCloseTo(c)
    }

    @Test
    fun testTimes_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(2))
        val c = Complex(Real.valueOf(-1), Real.valueOf(8))
        assertThat(a * b).isCloseTo(c)
    }

    @Test
    fun testTimes_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(2)
        val c = Complex(Real.valueOf(6), Real.valueOf(4))
        assertThat(a * b).isCloseTo(c)
    }

    @Test
    fun testDiv_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(1), Real.valueOf(2))
        val c = Complex(Real.valueOf("1.4"), Real.valueOf("-0.8"))
        assertThat(a / b).isCloseTo(c)
    }

    @Test
    fun testDiv_Complex_ByZero() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat {
            (a / Complex.ZERO).toString()
        }.isFailure().isInstanceOf(PrecisionOverflowError::class)
    }

    @Test
    fun testDiv_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(2)
        val c = Complex(Real.valueOf(1.5), Real.valueOf(1))
        assertThat(a / b).isCloseTo(c)
    }

    @Test
    fun testDiv_Real_ByZero() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat {
            (a / Real.ZERO).toString()
        }.isFailure().isInstanceOf(PrecisionOverflowError::class)
    }

    @Test
    fun testConjugate() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(3), Real.valueOf(-2))
        assertThat(a.conjugate).isCloseTo(b)
    }

    @Test
    fun testNorm() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat(a.norm).isCloseTo("3.60555127546398929312")
    }

    @Test
    fun testSquaredNorm() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat(a.squaredNorm).isCloseTo("13.00000000000000000000")
    }

    @Test
    fun testArgument_TopRightQuadrant() = assertThat(Complex(Real.valueOf(3), Real.valueOf(2)).argument).isCloseTo("0.58800260354756755125")

    @Test
    fun testArgument_TopLeftQuadrant() = assertThat(Complex(Real.valueOf(-3), Real.valueOf(2)).argument).isCloseTo("2.55359005004222568722")

    @Test
    fun testArgument_BottomLeftQuadrant() = assertThat(Complex(Real.valueOf(-3), Real.valueOf(-2)).argument).isCloseTo("-2.55359005004222568722")

    @Test
    fun testArgument_BottomRightQuadrant() = assertThat(Complex(Real.valueOf(3), Real.valueOf(-2)).argument).isCloseTo("-0.58800260354756755125")

    @Test
    fun testArgument_PlusReal() = assertThat(Complex(Real.valueOf(3), Real.ZERO).argument).isCloseTo("0.00000000000000000000")

    @Test
    fun testArgument_MinusReal() = assertThat(Complex(Real.valueOf(-3), Real.ZERO).argument).isCloseTo("3.14159265358979323846")

    @Test
    fun testArgument_PlusImag() = assertThat(Complex(Real.ZERO, Real.valueOf(2)).argument).isCloseTo("1.57079632679489661923")

    @Test
    fun testArgument_MinusImag() = assertThat(Complex(Real.ZERO, Real.valueOf(-2)).argument).isCloseTo("-1.57079632679489661923")

    @Test
    fun testArgument_Zero() = assertThat(Complex.ZERO.argument).isCloseTo("0.00000000000000000000")

    @Test
    fun testUnaryMinus() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(-3), Real.valueOf(-2))
        assertThat(-a).isCloseTo(b)
    }

    @Test
    fun testUnaryPlus() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat(+a).isCloseTo(a)
    }

    @Test
    fun testPow_Complex() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(2), Real.valueOf(1))
        val c = Complex(Real.valueOf("-5.60043034583960297990"), Real.valueOf("4.55775731029292673098"))
        assertThat(a.pow(b)).isCloseTo(c)
    }

    @Test
    fun testPow_Real() {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Real.valueOf(2)
        val c = Complex(Real.valueOf(5), Real.valueOf(12))
        assertThat(a.pow(b)).isCloseTo(c)
    }
}
