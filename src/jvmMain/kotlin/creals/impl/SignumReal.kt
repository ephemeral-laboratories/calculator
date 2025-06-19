package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.bigint.toBigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Constructive real representing sgn(x).
 */
internal class SignumReal(private val x: Real) : Real() {
    override fun approximate(precision: Int): BigInt {
        return x.signum(precision).toBigInt()
    }
}
