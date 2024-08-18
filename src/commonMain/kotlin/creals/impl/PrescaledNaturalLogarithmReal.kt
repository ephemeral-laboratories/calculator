package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.util.scale
import org.gciatto.kt.math.BigInteger

/**
 * Representation for ln(1 + op).
 */
internal class PrescaledNaturalLogarithmReal(private val op: Real) : SlowReal() {
    // Compute an approximation of ln(1+x) to precision
    // prec. This assumes |x| < 1/2.
    // It uses a Taylor series expansion.
    // Unfortunately there appears to be no way to take
    // advantage of old information.
    // Note: this is known to be a bad algorithm for
    // floating point.  Unfortunately, other alternatives
    // appear to require precomputed tabular information.
    override fun approximate(precision: Int): BigInteger {
        if (precision >= 0) return BIG0
        // conservative estimate > 0
        val iterationsNeeded = -precision
        // Claim: each intermediate term is accurate to `2*2^calc_precision`.
        // Total error is `2*iterations_needed*2^calc_precision` exclusive of error in op.
        // for error in op, truncation.
        val calcPrecision = (precision - boundLog2(2 * iterationsNeeded) - 4)
        val opPrecision = precision - 3
        val opApproximation = op.getApproximation(opPrecision)
        // Error analysis as for exponential.
        var xNth = opApproximation.scale(opPrecision - calcPrecision)
        var currentTerm = xNth // x**n
        var currentSum = currentTerm
        var n = 1
        // (-1)^(n-1)
        var currentSign = 1
        val maxTruncError = BIG1.shl(precision - 4 - calcPrecision)
        while (currentTerm.absoluteValue >= maxTruncError) {
            n += 1
            currentSign = -currentSign
            xNth = (xNth * opApproximation).scale(opPrecision)
            currentTerm = xNth / (n * currentSign)
            // x**n / (n * (-1)**(n-1))
            currentSum += currentTerm
        }
        return currentSum.scale(calcPrecision - precision)
    }
}
