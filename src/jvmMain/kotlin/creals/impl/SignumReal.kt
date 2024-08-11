package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import java.math.BigInteger

/**
 * Constructive real representing sgn(x).
 */
internal class SignumReal(private val x: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        return x.signum(precision).toBigInteger()
    }
}
