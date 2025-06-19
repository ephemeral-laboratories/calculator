package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.abs
import garden.ephemeral.calculator.creals.asin
import garden.ephemeral.calculator.creals.sqrt

/**
 * Real representation of `atan2(y, x)`.
 *
 * Note that the arguments are reversed from what you would intuitively expect,
 * for consistency with the order used by [kotlin.math.atan2].
 *
 * @param y the first parameter, corresponding to the Y axis value.
 * @param x the second parameter, corresponding to the X axis value.
 */
internal class Arctan2Real(private val y: Real, private val x: Real) : Real() {
    override fun approximate(precision: Int): BigInt {
        // Implemented by delegation. We deal with the special cases for 0s here as we know
        // the requested precision. Doing so at the [atan2] function itself was impossible,
        // as calling [signum] without precision causes overflows when the value is zero.
        val xSign = x.signum(precision)
        val ySign = y.signum(precision)

        val result = when (ySign) {
            1 -> when (xSign) {
                1 -> calcAbsAtan()
                -1 -> PI - calcAbsAtan()
                else -> HALF_PI
            }
            -1 -> when (xSign) {
                1 -> -calcAbsAtan()
                -1 -> -PI + calcAbsAtan()
                else -> -HALF_PI
            }
            else -> when (xSign) {
                1 -> ZERO
                -1 -> PI
                else -> ZERO
            }
        }

        return result.getApproximation(precision)
    }

    private fun calcAbsAtan(): Real {
        val a = abs(y) / abs(x)
        val a2 = a * a
        return asin(sqrt(a2 / (ONE + a2)))
    }
}
