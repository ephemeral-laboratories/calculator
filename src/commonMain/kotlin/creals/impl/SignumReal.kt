package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import org.gciatto.kt.math.BigInteger

/**
 * Constructive real representing sgn(x).
 */
internal class SignumReal(private val x: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        return BigInteger.of(x.signum(precision))
    }
}
