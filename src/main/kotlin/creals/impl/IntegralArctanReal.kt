package garden.ephemeral.calculator.creals.impl

import java.math.BigInteger

/**
 * The constructive real atan(1/n), where n is a small integer > base.
 * This gives a simple and moderately fast way to compute PI.
 */
internal class IntegralArctanReal(var op: Int) : SlowReal() {
    override fun approximate(precision: Int): BigInteger {
        if (precision >= 1) return BIG0
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
        val scaled1 = BIG1.shiftLeft(-calcPrecision)
        val bigOp = op.toBigInteger()
        val bigOpSquared = (op * op).toBigInteger()
        val opInverse = scaled1 / bigOp
        var currentPower = opInverse
        var currentTerm = opInverse
        var currentSum = opInverse
        var currentSign = 1
        var n = 1
        val maxTruncError = BIG1.shiftLeft(precision - 2 - calcPrecision)
        while (currentTerm.abs() >= maxTruncError) {
            checkForAbort()
            n += 2
            currentPower /= bigOpSquared
            currentSign = -currentSign
            currentTerm = currentPower / (currentSign * n).toBigInteger()
            currentSum += currentTerm
        }
        return scale(currentSum, calcPrecision - precision)
    }
}
