package garden.ephemeral.calculator.complex

import assertk.assertThat
import garden.ephemeral.calculator.creals.Real
import org.junit.jupiter.api.Test

class ComplexFunctionsTest {
    @Test
    fun testExp() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("-8.35853265093537158089"), Real.valueOf("18.26372704066676617145"))
        assertThat(exp(z)).isCloseTo(w)
    }

    @Test
    fun testLn() {
        val z = Complex(Real.valueOf("-8.35853265093537158089"), Real.valueOf("18.26372704066676617145"))
        val w = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat(ln(z)).isCloseTo(w)
    }

    @Test
    fun testSqrt() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("1.81735402102397062007"), Real.valueOf("0.55025052270033751106"))
        assertThat(sqrt(z)).isCloseTo(w)
    }

    @Test
    fun testSin() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("0.53092108624851980527"), Real.valueOf("-3.59056458998577995201"))
        assertThat(sin(z)).isCloseTo(w)
    }

    @Test
    fun testCos() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("-3.72454550491532256547"), Real.valueOf("-0.51182256998738460883"))
        assertThat(cos(z)).isCloseTo(w)
    }

    @Test
    fun testTan() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("-0.00988437503832249372"), Real.valueOf("0.96538587902213312428"))
        assertThat(tan(z)).isCloseTo(w)
    }

    @Test
    fun testAsin() {
        val z = Complex(Real.valueOf("0.53092108624851980527"), Real.valueOf("-3.59056458998577995201"))
        val w = Complex(Real.valueOf("0.14159265358979323846"), Real.valueOf("-2.00000000000000000000"))
        assertThat(asin(z)).isCloseTo(w)
    }

    @Test
    fun testAcos() {
        val z = Complex(Real.valueOf("-3.72454550491532256547"), Real.valueOf("-0.51182256998738460883"))
        val w = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat(acos(z)).isCloseTo(w)
    }

    @Test
    fun testAtan() {
        val z = Complex(Real.valueOf("-0.00988437503832249372"), Real.valueOf("0.96538587902213312428"))
        val w = Complex(Real.valueOf("-0.14159265358979323846"), Real.valueOf("2.00000000000000000002"))
        assertThat(atan(z)).isCloseTo(w)
    }

    @Test
    fun testSinh() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("-4.16890695996656435075"), Real.valueOf("9.15449914691142957347"))
        assertThat(sinh(z)).isCloseTo(w)
    }

    @Test
    fun testCosh() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("-4.18962569096880723013"), Real.valueOf("9.10922789375533659798"))
        assertThat(cosh(z)).isCloseTo(w)
    }

    @Test
    fun testTanh() {
        val z = Complex(Real.valueOf(3), Real.valueOf(2))
        val w = Complex(Real.valueOf("1.00323862735360980145"), Real.valueOf("-0.00376402564150424829"))
        assertThat(tanh(z)).isCloseTo(w)
    }

    @Test
    fun testAsinh() {
        val z = Complex(Real.valueOf("-4.16890695996656435075"), Real.valueOf("9.15449914691142957347"))
        val w = Complex(Real.valueOf("-3.00000000000000000000"), Real.valueOf("1.14159265358979323846"))
        assertThat(asinh(z)).isCloseTo(w)
    }

    @Test
    fun testAcosh() {
        val z = Complex(Real.valueOf("-4.18962569096880723013"), Real.valueOf("9.10922789375533659798"))
        val w = Complex(Real.valueOf(3), Real.valueOf(2))
        assertThat(acosh(z)).isCloseTo(w)
    }

    @Test
    fun testAtanh() {
        val z = Complex(Real.valueOf("1.00323862735360980145"), Real.valueOf("-0.00376402564150424829"))
        val w = Complex(Real.valueOf("2.99999999999999999997"), Real.valueOf("-1.14159265358979323892"))
        assertThat(atanh(z)).isCloseTo(w)
    }
}