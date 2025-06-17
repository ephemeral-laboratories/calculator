package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.util.scale
import java.math.BigInteger

/**
 * Representation of the exponential of a constructive real.
 * Uses a Taylor series expansion.  Assumes x < 1/2.
 * Note: this is known to be a bad algorithm for floating point.
 * Unfortunately, other alternatives appear to require precomputed information.
 */
internal class PrescaledExponentialReal(private val op: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        if (precision >= 1) return BIG0
        val iterationsNeeded = -precision / 2 + 2 // conservative estimate > 0.
        //  Claim: each intermediate term is accurate
        //  to 2*2^calc_precision.
        //  Total rounding error in series computation is
        //  2*iterations_needed*2^calc_precision,
        //  exclusive of error in op.
        // for error in op, truncation.
        val calcPrecision = (precision - boundLog2(2 * iterationsNeeded) - 4)
        val opPrecision = precision - 3
        val opApproximation = op.getApproximation(opPrecision)
        // Error in argument results in error of < 3/8 ulp.
        // Sum of term eval. rounding error is < 1/16 ulp.
        // Series truncation error < 1/16 ulp.
        // Final rounding error is <= 1/2 ulp.
        // Thus final error is < 1 ulp.
        val scaled1 = BIG1 shl (-calcPrecision)
        var currentTerm = scaled1
        var currentSum = scaled1
        var n = 0
        val maxTruncError = BIG1.shl (precision - 4 - calcPrecision)
        while (currentTerm.abs() >= maxTruncError) {
            checkForAbort()
            n += 1
            // current_term = current_term * op / n
            currentTerm = (currentTerm * opApproximation).scale(opPrecision)
            currentTerm /= n.toBigInteger()
            currentSum += currentTerm
        }
        return currentSum.scale(calcPrecision - precision)
    }
}
