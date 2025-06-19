package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Representation of a number that may not have been completely
 * evaluated, but is assumed to be an integer.  Hence, we never
 * evaluate beyond the decimal point.
 */
internal class AssumedIntReal(val value: Real) : Real() {
    override fun approximate(precision: Int): BigInt {
        return if (precision >= 0) {
            value.getApproximation(precision)
        } else {
            value.getApproximation(0).scale(-precision)
        }
    }
}
