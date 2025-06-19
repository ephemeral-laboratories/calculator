package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Representation of:
 *  op1  if selector < 0
 *  op2  if selector >= 0
 * Assumes x = y if s = 0
 */
internal class SelectReal(private val selector: Real, private val whenNegative: Real, private val whenPositive: Real) : Real() {
    private var selectorSign: Int = selector.signum(-20)

    override fun approximate(precision: Int): BigInt {
        if (selectorSign < 0) return whenNegative.getApproximation(precision)
        if (selectorSign > 0) return whenPositive.getApproximation(precision)
        val whenNegativeApproximation = whenNegative.getApproximation(precision - 1)
        val whenPositiveApproximation = whenPositive.getApproximation(precision - 1)
        val diff = (whenNegativeApproximation - whenPositiveApproximation).abs()
        if (diff <= BigInt.One) {
            // close enough; use either
            return whenNegativeApproximation.scale(-1)
        }
        // op1 and op2 are different; selector != 0;
        // safe to get sign of selector.
        return if (selector.signum() < 0) {
            selectorSign = -1
            whenNegativeApproximation.scale(-1)
        } else {
            selectorSign = 1
            whenPositiveApproximation.scale(-1)
        }
    }
}
