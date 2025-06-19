package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Representation of a constructive real multiplied by `2**n`.
 */
internal class ShiftedReal(private val op: Real, private val count: Int) : Real() {
    override fun approximate(precision: Int): BigInt {
        return op.getApproximation(precision - count)
    }
}
