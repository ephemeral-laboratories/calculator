package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import java.math.BigInteger

/**
 * Representation of an integer constant.
 */
internal class IntegerConstantReal(var value: BigInteger) : Real() {
    override fun approximate(precision: Int): BigInteger {
        return scale(value, -precision)
    }
}
