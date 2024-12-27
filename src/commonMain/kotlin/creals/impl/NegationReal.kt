package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import org.gciatto.kt.math.BigInteger

/**
 * Representation of the negation of a constructive real.
 */
internal class NegationReal(private val op: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        return -op.getApproximation(precision)
    }
}
