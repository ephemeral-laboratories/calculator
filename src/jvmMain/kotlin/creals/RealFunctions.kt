package garden.ephemeral.calculator.creals

import garden.ephemeral.calculator.creals.impl.Arctan2Real
import garden.ephemeral.calculator.creals.impl.PrescaledCosineReal
import garden.ephemeral.calculator.creals.impl.PrescaledExponentialReal
import garden.ephemeral.calculator.creals.impl.PrescaledNaturalLogarithmReal
import garden.ephemeral.calculator.creals.impl.SelectReal
import garden.ephemeral.calculator.creals.impl.SignumReal
import garden.ephemeral.calculator.creals.impl.SquareRootReal

/**
 * Composes two functions.
 *
 * @param f1 the first function.
 * @param f2 the second function.
 * @return the composition of the two functions.
 */
fun compose(f1: (Real) -> Real, f2: (Real) -> Real): (Real) -> Real = { x -> f1(f2(x)) }

/**
 * The real number `x` if `this` < 0, or `y` otherwise.
 * Requires `x` = `y` if `this` = 0.
 * Since comparisons may diverge, this is often
 * a useful alternative to conditionals.
 */
fun select(selector: Real, whenNegative: Real, whenPositive: Real): Real {
    return SelectReal(selector, whenNegative, whenPositive)
}

/**
 * The maximum of two constructive reals.
 */
fun max(x: Real, y: Real) = select(x - y, y, x)

/**
 * The minimum of two constructive reals.
 */
fun min(x: Real, y: Real) = select(x - y, x, y)

/**
 * The absolute value of a constructive reals.
 * Note that this cannot be written as a conditional.
 */
fun abs(x: Real) = select(x, -x, x)

/**
 * The signum value of a constructive real.
 * This avoids the problem with [Real.signum] blowing out precision when the value is 0,
 * by using a constructive real for the result, thus delaying the specification of precision.
 */
fun sgn(x: Real): Real = SignumReal(x)

/**
 * The exponential function, that is, `e**this`.
 */
fun exp(x: Real): Real {
    val lowPrecision = -10
    val roughApproximation = x.getApproximation(lowPrecision)
    if (roughApproximation.signum() < 0) return exp(-x).reciprocal()
    if (roughApproximation > Real.BIG2) {
        val squareRoot = exp(x.shr(1))
        return squareRoot * squareRoot
    } else {
        return PrescaledExponentialReal(x)
    }
}

/**
 * The natural (base e) logarithm.
 */
fun ln(x: Real): Real {
    val lowPrec = -4
    val roughApprox = x.getApproximation(lowPrec) /* In sixteenths */
    if (roughApprox < Real.BIG0) {
        throw ArithmeticException()
    }
    if (roughApprox <= Real.lowLnLimit) {
        return -ln(x.reciprocal())
    }
    if (roughApprox >= Real.highLnLimit) {
        if (roughApprox <= Real.scaled4) {
            val quarter = ln(sqrt(sqrt(x)))
            return quarter shl 2
        } else {
            val extraBits = roughApprox.bitLength() - 3
            val scaledResult = ln(x.shr(extraBits))
            return scaledResult + Real.valueOf(extraBits) * Real.LN2
        }
    }
    return simpleLn(x)
}

/**
 * Natural log of 2.  Needed for some pre-scaling below.
 *
 * `ln(2) = 7ln(10/9) - 2ln(25/24) + 3ln(81/80)`
 */
internal fun simpleLn(x: Real): Real {
    return PrescaledNaturalLogarithmReal(x - Real.ONE)
}

/**
 * The square root of a constructive real.
 */
fun sqrt(x: Real): Real {
    return SquareRootReal(x)
}

/**
 * The trigonometric sine function.
 */
fun sin(x: Real) = cos(Real.HALF_PI - x)

/**
 * The trigonometric cosine function.
 */
fun cos(x: Real): Real {
    val roughApproximation = x.getApproximation(-1)
    val absoluteRoughApproximation = roughApproximation.abs()
    if (absoluteRoughApproximation >= Real.BIG6) {
        // Subtract multiples of PI
        val multiplier = roughApproximation / Real.BIG6
        val adjustment = Real.PI * Real.Companion.valueOf(multiplier)
        return if (multiplier.and(Real.BIG1).signum() != 0) {
            -cos(x - adjustment)
        } else {
            cos(x - adjustment)
        }
    } else if (absoluteRoughApproximation >= Real.BIG2) {
        // Scale further with double angle formula
        val cosHalf = cos(x.shr(1))
        return ((cosHalf * cosHalf) shl 1) - Real.ONE
    } else {
        return PrescaledCosineReal(x)
    }
}

/**
 * The trigonometric tangent function.
 */
fun tan(x: Real) = sin(x) / cos(x)

/**
 * The inverse (arc) trigonometric sine function.
 */
fun asin(x: Real): Real = asinInternal(x)
private val asinInternal by lazy {
    inverseMonotone(::sin, -Real.HALF_PI, Real.HALF_PI)
}

/**
 * The inverse (arc) trigonometric cosine function.
 */
fun acos(x: Real): Real {
    return Real.HALF_PI - asin(x)
}

/**
 * The inverse (arc) trigonometric tangent function.
 */
fun atan(x: Real): Real {
    val x2 = x * x
    val absSinAtan = sqrt(x2 / (Real.ONE + x2))
    val sinAtan = select(x, -absSinAtan, absSinAtan)
    return asin(sinAtan)
}

/**
 * The inverse (arc) trigonometric tangent function.
 *
 * This two-arg version gives the right results for all four quadrants
 * as well as the special cases for x=0 and/or y=0.
 *
 * Note that the arguments are reversed from what you would intuitively expect,
 * for consistency with the order used by [kotlin.math.atan2].
 *
 * @param y the first parameter, corresponding to the Y axis value.
 * @param x the second parameter, corresponding to the X axis value.
 */
fun atan2(y: Real, x: Real): Real {
    return Arctan2Real(y, x)
}

/**
 * The hyperbolic sine function.
 */
fun sinh(x: Real): Real {
    return (exp(x) - exp(-x)) * Real.ONE_HALF
}

/**
 * The hyperbolic cosine function.
 */
fun cosh(x: Real): Real {
    return (exp(x) + exp(-x)) * Real.ONE_HALF
}

/**
 * The hyperbolic tangent function.
 */
fun tanh(x: Real): Real {
    // Same as sinh(argument) / cosh(argument), but with fewer redundant calculations
    val expX = exp(x)
    val expMX = exp(-x)
    return (expX - expMX) / (expX + expMX)
}

/**
 * The inverse (area) hyperbolic sine function.
 */
fun asinh(x: Real): Real {
    return ln(sqrt(x * x + Real.ONE) + x)
}

/**
 * The inverse (area) hyperbolic cosine function.
 */
fun acosh(x: Real): Real {
    return ln(x + sqrt(x - Real.ONE) * sqrt(x + Real.ONE))
}

/**
 * The inverse (area) hyperbolic tangent function.
 */
fun atanh(x: Real): Real {
    return Real.ONE_HALF * (ln(x + Real.ONE) - ln(Real.ONE - x))
}
