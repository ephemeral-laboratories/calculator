package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Representation of the negation of a constructive real.
 */
internal class NegationReal(private val op: Real) : Real() {
    override fun approximate(precision: Int): BigInt {
        return -op.getApproximation(precision)
    }
}
