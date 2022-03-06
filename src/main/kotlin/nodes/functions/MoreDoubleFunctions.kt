package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.math.complex.complexSqrt
import kotlin.math.*

internal fun sec(x: Double) = 1.0 / cos(x)
internal fun csc(x: Double) = 1.0 / sin(x)
internal fun cot(x: Double) = 1.0 / tan(x)
internal fun sech(x: Double) = 1.0 / cosh(x)
internal fun csch(x: Double) = 1.0 / sinh(x)
internal fun coth(x: Double) = 1.0 / tanh(x)

internal fun asec(x: Double) = acos(1.0 / x)
internal fun acsc(x: Double) = asin(1.0 / x)
internal fun acot(x: Double) = atan(1.0 / x)
internal fun asech(x: Double) = acosh(1.0 / x)
internal fun acsch(x: Double) = asinh(1.0 / x)
internal fun acoth(x: Double) = atanh(1.0 / x)

fun maybeComplexSqrt(x: Double): Any {
    return if (x < 0.0) {
        complexSqrt(x)
    } else {
        sqrt(x)
    }
}
