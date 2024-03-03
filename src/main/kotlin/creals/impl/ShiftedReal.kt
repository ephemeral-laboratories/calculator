package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import java.math.BigInteger

/**
 * Representation of a constructive real multiplied by `2**n`.
 */
internal class ShiftedReal(private var op: Real, private var count: Int) : Real() {
    override fun approximate(precision: Int): BigInteger {
        return op.getApproximation(precision - count)
    }
}
