package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import java.math.BigInteger

/**
 * Representation of the sum of 2 constructive reals.
 */
internal class AddReal(private var op1: Real, private var op2: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        // Args need to be evaluated so that each error is < 1/4 ulp.
        // Rounding error from the cale call is <= 1/2 ulp, so that
        // final error is < 1 ulp.
        return scale(op1.getApproximation(precision - 2) + op2.getApproximation(precision - 2), -2)
    }
}
