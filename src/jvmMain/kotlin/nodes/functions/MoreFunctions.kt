package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.complex.acos
import garden.ephemeral.calculator.complex.acosh
import garden.ephemeral.calculator.complex.asin
import garden.ephemeral.calculator.complex.asinh
import garden.ephemeral.calculator.complex.atan
import garden.ephemeral.calculator.complex.atanh
import garden.ephemeral.calculator.complex.cos
import garden.ephemeral.calculator.complex.cosh
import garden.ephemeral.calculator.complex.sin
import garden.ephemeral.calculator.complex.sinh
import garden.ephemeral.calculator.complex.tan
import garden.ephemeral.calculator.complex.tanh
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.abs
import garden.ephemeral.calculator.creals.acos
import garden.ephemeral.calculator.creals.acosh
import garden.ephemeral.calculator.creals.asin
import garden.ephemeral.calculator.creals.asinh
import garden.ephemeral.calculator.creals.atan
import garden.ephemeral.calculator.creals.atanh
import garden.ephemeral.calculator.creals.cos
import garden.ephemeral.calculator.creals.cosh
import garden.ephemeral.calculator.creals.sin
import garden.ephemeral.calculator.creals.sinh
import garden.ephemeral.calculator.creals.sqrt
import garden.ephemeral.calculator.creals.tan
import garden.ephemeral.calculator.creals.tanh

internal fun sec(x: Real) = Real.ONE / cos(x)
internal fun sec(z: Complex) = Complex.ONE / cos(z)
internal fun csc(x: Real) = Real.ONE / sin(x)
internal fun csc(z: Complex) = Complex.ONE / sin(z)
internal fun cot(x: Real) = Real.ONE / tan(x)
internal fun cot(z: Complex) = Complex.ONE / tan(z)
internal fun sech(x: Real) = Real.ONE / cosh(x)
internal fun sech(z: Complex) = Complex.ONE / cosh(z)
internal fun csch(x: Real) = Real.ONE / sinh(x)
internal fun csch(z: Complex) = Complex.ONE / sinh(z)
internal fun coth(x: Real) = Real.ONE / tanh(x)
internal fun coth(z: Complex) = Complex.ONE / tanh(z)

internal fun asec(x: Real) = acos(Real.ONE / x)
internal fun asec(z: Complex) = acos(Complex.ONE / z)
internal fun acsc(x: Real) = asin(Real.ONE / x)
internal fun acsc(z: Complex) = asin(Complex.ONE / z)
internal fun acot(x: Real) = atan(Real.ONE / x)
internal fun acot(z: Complex) = atan(Complex.ONE / z)
internal fun asech(x: Real) = acosh(Real.ONE / x)
internal fun asech(z: Complex) = acosh(Complex.ONE / z)
internal fun acsch(x: Real) = asinh(Real.ONE / x)
internal fun acsch(z: Complex) = asinh(Complex.ONE / z)
internal fun acoth(x: Real) = atanh(Real.ONE / x)
internal fun acoth(z: Complex) = atanh(Complex.ONE / z)

internal fun maybeComplexSqrt(x: Real): Any {
    // FIXME: Strictly speaking, we should be delaying this decision depending on the requested precision.
    //        But select() only works for reals, and we don't have "constructive complex" values.
    return if (x.signum(-20) < 0) {
        Complex(Real.ZERO, sqrt(abs(x)))
    } else {
        sqrt(x)
    }
}
