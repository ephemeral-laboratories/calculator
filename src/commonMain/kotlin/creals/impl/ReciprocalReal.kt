package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import org.gciatto.kt.math.BigInteger

/**
 * Representation of the multiplicative inverse of a constructive real.
 * Should use Newton iteration to refine estimates.
 */
internal class ReciprocalReal(private val op: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        val msd = op.msd()
        val invMsd = 1 - msd
        val digitsNeeded = invMsd - precision + 3
        // Number of SIGNIFICANT digits needed for
        // argument, excl. msd position, which may
        // be fictitious, since msd routine can be
        // off by 1.  Roughly 1 extra digit is
        // needed since the relative error is the
        // same in the argument and result, but
        // this isn't quite the same as the number
        // of significant digits.  Another digit
        // is needed to compensate for slop in the
        // calculation.
        // One further bit is required, since the
        // final rounding introduces a 0.5 ulp
        // error.
        val precNeeded = msd - digitsNeeded
        val logScaleFactor = -precision - precNeeded
        if (logScaleFactor < 0) return BIG0
        val dividend = BIG1.shl(logScaleFactor)
        val scaledDivisor = op.getApproximation(precNeeded)
        val absScaledDivisor = scaledDivisor.absoluteValue
        val adjDividend = dividend + absScaledDivisor.shr(1)
        // Adjustment so that final result is rounded.
        val result = adjDividend / absScaledDivisor
        return if (scaledDivisor.signum < 0) {
            -result
        } else {
            result
        }
    }
}
