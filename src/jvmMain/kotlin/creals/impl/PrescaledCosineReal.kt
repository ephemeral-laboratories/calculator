package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.bigint.toBigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Representation of the cosine of a constructive real.
 * Uses a Taylor series expansion.  Assumes |x| < 1.
 */
internal class PrescaledCosineReal(private val op: Real) : SlowReal() {
    override fun approximate(precision: Int): BigInt {
        if (precision >= 1) return BigInt.Zero
        val iterationsNeeded = -precision / 2 + 4 // conservative estimate > 0.
        //  Claim: each intermediate term is accurate
        //  to 2*2^calc_precision.
        //  Total rounding error in series computation is
        //  2*iterations_needed*2^calc_precision,
        //  exclusive of error in op.
        val calcPrecision = (precision - boundLog2(2 * iterationsNeeded) - 4) // for error in op, truncation.
        val opPrecision = precision - 2
        val opApproximation = op.getApproximation(opPrecision)
        // Error in argument results in error of < 1/4 ulp.
        // Cumulative arithmetic rounding error is < 1/16 ulp.
        // Series truncation error < 1/16 ulp.
        // Final rounding error is <= 1/2 ulp.
        // Thus final error is < 1 ulp.
        val maxTruncError = BigInt.One shl (precision - 4 - calcPrecision)
        var n = 0
        var currentTerm = BigInt.One shl (-calcPrecision)
        var currentSum = currentTerm
        while (currentTerm.abs() >= maxTruncError) {
            checkForAbort()
            n += 2
            // current_term = - current_term * op * op / n * (n - 1)
            currentTerm = (currentTerm * opApproximation).scale(opPrecision)
            currentTerm = (currentTerm * opApproximation).scale(opPrecision)
            val divisor = (-n).toBigInt() * (n - 1).toBigInt()
            currentTerm /= divisor
            currentSum += currentTerm
        }
        return currentSum.scale(calcPrecision - precision)
    }
}
