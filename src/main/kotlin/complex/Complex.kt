package garden.ephemeral.calculator.complex

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.abs
import garden.ephemeral.calculator.creals.atan2
import garden.ephemeral.calculator.creals.sqrt

/**
 * A complex number.
 *
 * @constructor constructs the complex number from its real and imaginary components.
 * @property real the real component.
 * @property imag the imaginary component.
 */
class Complex(val real: Real, val imag: Real) {
    val conjugate: Complex get() = Complex(real, -imag)

    /**
     * The norm, i.e. the magnitude.
     */
    val norm get() = sqrt(squaredNorm)

    /**
     * The square of the norm.
     *
     * Because calculating the norm requires computing a square root, using
     * `squaredNorm` will be faster than getting `norm` and squaring it.
     */
    val squaredNorm get() = real * real + imag * imag

    /**
     * The argument, i.e. the counterclockwise angle from the real axis.
     */
    val argument: Real get() = atan2(imag, real)

    operator fun plus(other: Complex) = Complex(real + other.real, imag + other.imag)
    operator fun plus(other: Real) = Complex(real + other, imag)

    operator fun minus(other: Complex) = Complex(real - other.real, imag - other.imag)
    operator fun minus(other: Real) = Complex(real - other, imag)

    operator fun times(other: Complex) = Complex(
        real * other.real - imag * other.imag,
        real * other.imag + imag * other.real,
    )
    operator fun times(other: Real) = Complex(real * other, imag * other)

    operator fun div(other: Complex) = (this * other.conjugate) / (other * other.conjugate).real
    operator fun div(other: Real) = times(other.reciprocal())

    operator fun unaryMinus(): Complex = Complex(-real, -imag)
    operator fun unaryPlus(): Complex = this

    fun pow(exponent: Complex) = exp(ln(this) * exponent)
    fun pow(exponent: Real) = exp(ln(this) * exponent)

    fun toString(pointsOfPrecision: Int = 10, radix: Int = 10): String {
        val realSignum = real.signum(-pointsOfPrecision)
        val realAbsString = abs(real).toString(pointsOfPrecision = pointsOfPrecision, radix = radix)
        val realPrefix = if (realSignum < 0) "-" else ""
        val imagSignum = imag.signum(-pointsOfPrecision)
        val imagAbsString = abs(imag).toString(pointsOfPrecision = pointsOfPrecision, radix = radix)
        val imagPrefix = if (imagSignum < 0) "-" else ""

        return when {
            realSignum == 0 && imagSignum == 0 -> realAbsString
            realSignum == 0 -> "$imagPrefix${imagAbsString}i"
            imagSignum == 0 -> "$realPrefix$realAbsString"
            imagSignum < 0 -> "$realPrefix$realAbsString - ${imagAbsString}i"
            else -> "$realPrefix$realAbsString + ${imagAbsString}i"
        }
    }

    override fun toString() = toString(pointsOfPrecision = 10, radix = 10)

    companion object {
        val ZERO = Complex(Real.ZERO, Real.ZERO)
        val ONE = Complex(Real.ONE, Real.ZERO)
        val ONE_HALF = Complex(Real.ONE_HALF, Real.ZERO)
        val MINUS_ONE = Complex(Real.MINUS_ONE, Real.ZERO)
        val I = Complex(Real.ZERO, Real.ONE)
        val HALF_I = Complex(Real.ZERO, Real.ONE_HALF)
        val MINUS_I = Complex(Real.ZERO, Real.MINUS_ONE)
        val PI = Complex(Real.PI, Real.ZERO)
        val HALF_PI = Complex(Real.HALF_PI, Real.ZERO)
    }
}
