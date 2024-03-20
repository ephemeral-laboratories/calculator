package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.util.scale
import java.math.BigInteger

/**
 * Representation of an integer constant.
 */
internal class IntegerConstantReal(val value: BigInteger) : Real() {
    override fun approximate(precision: Int): BigInteger {
        return value.scale(-precision)
    }
}
