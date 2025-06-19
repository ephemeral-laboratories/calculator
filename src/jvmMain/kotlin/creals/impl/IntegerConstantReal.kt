package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Representation of an integer constant.
 */
internal class IntegerConstantReal(val value: BigInt) : Real() {
    override fun approximate(precision: Int): BigInt {
        return value.scale(-precision)
    }
}
