package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.util.scale
import java.math.BigInteger

/**
 * Representation of sqrt(x).
 */
internal class SquareRootReal(private val op: Real) : Real() {
    private val fpPrecision: Int = 50 // Conservative estimate of number of

    // significant bits in double precision
    // computation.
    private val fpOpPrecision: Int = 60

    override fun approximate(precision: Int): BigInteger {
        val maxPrecNeeded = 2 * precision - 1
        val msd = op.msd(maxPrecNeeded)
        if (msd <= maxPrecNeeded) return BIG0
        val resultMsd = msd / 2 // +- 1
        val resultDigits = resultMsd - precision // +- 2
        if (resultDigits > fpPrecision) {
            // Compute less precise approximation and use a Newton iter.
            val approximationDigits = resultDigits / 2 + 6
            // This should be conservative.  Is fewer enough?
            val approximationPrecision = resultMsd - approximationDigits
            val lastApproximation = getApproximation(approximationPrecision)
            val producedPrecision = 2 * approximationPrecision
            val opApproximation = op.getApproximation(producedPrecision)
            // Slightly fewer might be enough;
            // Compute (lastApproximation * lastApproximation + opApproximation)/(lastApproximation/2)
            // while adjusting the scaling to make everything work
            val producedPrecisionScaledNumerator = (lastApproximation * lastApproximation) + opApproximation
            val scaledNumerator = producedPrecisionScaledNumerator.scale(approximationPrecision - precision)
            val shiftedResult = scaledNumerator / lastApproximation
            return (shiftedResult + BIG1).shiftRight(1)
        } else {
            // Use a double precision floating point approximation.
            // Make sure all precisions are even
            val opPrecision = (msd - fpOpPrecision) and 1.inv()
            val workingPrecision = opPrecision - fpOpPrecision
            val scaledBiApproximation = op.getApproximation(opPrecision).shiftLeft(fpOpPrecision)
            val scaledApproximation = scaledBiApproximation.toDouble()
            if (scaledApproximation < 0.0) throw ArithmeticException()
            val scaledFpSqrt = kotlin.math.sqrt(scaledApproximation)
            val scaledSqrt = (scaledFpSqrt.toLong()).toBigInteger()
            val shiftCount = workingPrecision / 2 - precision
            return scaledSqrt.shiftLeft(shiftCount)
        }
    }
}
