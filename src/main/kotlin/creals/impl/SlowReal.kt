package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.util.scale
import java.math.BigInteger

/**
 * A specialization of [Real] for cases in which [approximate] calls
 * to increase evaluation precision are somewhat expensive.
 *
 * If we need to (re)evaluate, we speculatively evaluate to slightly
 * higher precision, minimizing reevaluations.
 * Note that this requires any arguments to be evaluated to higher
 * precision than absolutely necessary.  It can thus potentially
 * result in lots of wasted effort, and should be used judiciously.
 *
 * This assumes that the order of magnitude of the number is roughly one.
 */
internal abstract class SlowReal : Real() {
    // Overridden to perform the change in calculated precision
    override fun calcApproximation(precision: Int): BigInteger {
        val evalPrecision = if (precision >= MAX_PRECISION) {
            MAX_PRECISION
        } else {
            (precision - PRECISION_INCREMENT + 1) and (PRECISION_INCREMENT - 1).inv()
        }

        val result = approximate(evalPrecision)
        minPrecision = evalPrecision
        maxApproximation = result
        return result.scale(evalPrecision - precision)
    }

    companion object {
        const val MAX_PRECISION: Int = -64
        const val PRECISION_INCREMENT: Int = 32
    }
}
