package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.bigint.toBigInt
import garden.ephemeral.calculator.creals.Real

/**
 * Representation of sqrt(x).
 */
internal class SquareRootReal(private val op: Real) : Real() {
    /**
     * Conservative estimate of the significant bits in double precision computation
     * (the true value is 52)
     */
    private val fpPrecision: Int = 50

    private val fpOpPrecision: Int = 60

    override fun approximate(precision: Int): BigInt {
        val maxPrecNeeded = 2 * precision - 1
        val msd = op.msd(maxPrecNeeded)
        if (msd <= maxPrecNeeded) return BigInt.Zero
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
            return (shiftedResult + BigInt.One) shr 1
        } else {
            // Use a double-precision floating point approximation.
            // Make sure all precisions are even
            val opPrecision = (msd - fpOpPrecision) and 1.inv()
            val workingPrecision = opPrecision - fpOpPrecision
            val scaledBiApproximation = op.getApproximation(opPrecision) shl fpOpPrecision
            val scaledApproximation = scaledBiApproximation.toDouble()
            if (scaledApproximation < 0.0) throw ArithmeticException()
            val scaledFpSqrt = kotlin.math.sqrt(scaledApproximation)
            val scaledSqrt = (scaledFpSqrt.toLong()).toBigInt()
            val shiftCount = workingPrecision / 2 - precision
            return scaledSqrt shl shiftCount
        }
    }
}
