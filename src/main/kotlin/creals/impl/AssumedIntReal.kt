package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import java.math.BigInteger

/**
 * Representation of a number that may not have been completely
 * evaluated, but is assumed to be an integer.  Hence, we never
 * evaluate beyond the decimal point.
 */
internal class AssumedIntReal(var value: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        return if (precision >= 0) {
            value.getApproximation(precision)
        } else {
            scale(value.getApproximation(0), -precision)
        }
    }
}
