package garden.ephemeral.calculator.complex

import garden.ephemeral.calculator.creals.PrecisionOverflowError
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.shouldBeCloseTo
import garden.ephemeral.calculator.util.row
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ComplexTest : FreeSpec({
    "toString" {
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

    "plus" - {
        "with complex argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Complex(Real.valueOf(1), Real.valueOf(1))
            val c = Complex(Real.valueOf(4), Real.valueOf(3))
            (a + b) shouldBeCloseTo c
        }

        "with real argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Real.valueOf(1)
            val c = Complex(Real.valueOf(4), Real.valueOf(2))
            (a + b) shouldBeCloseTo c
        }
    }

    "minus" - {
        "with complex argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Complex(Real.valueOf(1), Real.valueOf(1))
            val c = Complex(Real.valueOf(2), Real.valueOf(1))
            (a - b) shouldBeCloseTo c
        }

        "with real argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Real.valueOf(1)
            val c = Complex(Real.valueOf(2), Real.valueOf(2))
            (a - b) shouldBeCloseTo c
        }
    }

    "times" - {
        "with complex argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Complex(Real.valueOf(1), Real.valueOf(2))
            val c = Complex(Real.valueOf(-1), Real.valueOf(8))
            (a * b) shouldBeCloseTo c
        }

        "with real argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Real.valueOf(2)
            val c = Complex(Real.valueOf(6), Real.valueOf(4))
            (a * b) shouldBeCloseTo c
        }
    }

    "div" - {
        "with complex argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Complex(Real.valueOf(1), Real.valueOf(2))
            val c = Complex(Real.valueOf("1.4"), Real.valueOf("-0.8"))
            (a / b) shouldBeCloseTo c
        }

        "for complex zero" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            shouldThrow<PrecisionOverflowError> {
                (a / Complex.ZERO).toString()
            }
        }

        "for real argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Real.valueOf(2)
            val c = Complex(Real.valueOf(1.5), Real.valueOf(1))
            (a / b) shouldBeCloseTo c
        }

        "for real zero" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))

            shouldThrow<PrecisionOverflowError> {
                (a / Real.ZERO).toString()
            }
        }
    }

    "conjugate" {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(3), Real.valueOf(-2))
        a.conjugate shouldBeCloseTo b
    }

    "norm" {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        a.norm shouldBeCloseTo "3.60555127546398929312"
    }

    "squaredNorm" {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        a.squaredNorm shouldBeCloseTo "13.00000000000000000000"
    }

    "argument" - {
        withData(
            row(-3, 2, "2.55359005004222568722", "top left quadrant"),
            row(-3, -2, "-2.55359005004222568722", "bottom left quadrant"),
            row(3, -2, "-0.58800260354756755125", "bottom right quadrant"),
            row(3, 0, "0.00000000000000000000", "positive real"),
            row(-3, 0, "3.14159265358979323846", "negative real"),
            row(0, 2, "1.57079632679489661923", "positive imaginary"),
            row(0, -2, "-1.57079632679489661923", "negative imaginary"),
            row(0, 0, "0.00000000000000000000", "zero"),
        ) { (x, y, expected, _) ->
            Complex(Real.valueOf(x), Real.valueOf(y)).argument shouldBeCloseTo expected
        }
    }

    "unary minus" {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        val b = Complex(Real.valueOf(-3), Real.valueOf(-2))
        (-a) shouldBeCloseTo b
    }

    "unary plus" {
        val a = Complex(Real.valueOf(3), Real.valueOf(2))
        (+a) shouldBeCloseTo a
    }

    "pow" - {
        "for complex argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Complex(Real.valueOf(2), Real.valueOf(1))
            val c = Complex(Real.valueOf("-5.60043034583960297990"), Real.valueOf("4.55775731029292673098"))
            a.pow(b) shouldBeCloseTo c
        }

        "for real argument" {
            val a = Complex(Real.valueOf(3), Real.valueOf(2))
            val b = Real.valueOf(2)
            val c = Complex(Real.valueOf(5), Real.valueOf(12))
            a.pow(b) shouldBeCloseTo c
        }
    }
})
