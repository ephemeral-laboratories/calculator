package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.bigint.toBigInt

/**
 * The constructive real atan(1/n), where n is a small integer > base.
 * This gives a simple and moderately fast way to compute PI.
 */
internal class IntegralArctanReal(private val op: Int) : SlowReal() {
    override fun approximate(precision: Int): BigInt {
        if (precision >= 1) return BigInt.Zero
        // conservative estimate > 0
        val iterationsNeeded = -precision / 2 + 2
        // Claim: each intermediate term is accurate to `2*base^calc_precision`.
        // Total rounding error in series computation is `2*iterations_needed*base^calc_precision`,
        // exclusive of error in op. for error in op, truncation.
        val calcPrecision = (precision - boundLog2(2 * iterationsNeeded) - 2)
        // Error in argument results in error of < 3/8 ulp.
        // Cumulative arithmetic rounding error is < 1/4 ulp.
        // Series truncation error < 1/4 ulp.
        // Final rounding error is <= 1/2 ulp.
        // Thus, final error is < 1 ulp.
        val scaled1 = BigInt.One shl (-calcPrecision)
        val bigOp = op.toBigInt()
        val bigOpSquared = (op * op).toBigInt()
        val opInverse = scaled1 / bigOp
        var currentPower = opInverse
        var currentTerm = opInverse
        var currentSum = opInverse
        var currentSign = 1
        var n = 1
        val maxTruncError = BigInt.One shl (precision - 2 - calcPrecision)
        while (currentTerm.abs() >= maxTruncError) {
            checkForAbort()
            n += 2
            currentPower /= bigOpSquared
            currentSign = -currentSign
            currentTerm = currentPower / (currentSign * n).toBigInt()
            currentSum += currentTerm
        }
        return currentSum.scale(calcPrecision - precision)
    }
}
