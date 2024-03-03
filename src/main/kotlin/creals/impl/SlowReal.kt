package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
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
        val evalPrecision = if (precision >= maxPrecision) {
            maxPrecision
        } else {
            (precision - precisionIncrement + 1) and (precisionIncrement - 1).inv()
        }

        val result = approximate(evalPrecision)
        minPrecision = evalPrecision
        maxApproximation = result
        return scale(result, evalPrecision - precision)
    }

    companion object {
        var maxPrecision: Int = -64
        var precisionIncrement: Int = 32
    }
}
