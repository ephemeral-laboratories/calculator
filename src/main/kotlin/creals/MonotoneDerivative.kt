package garden.ephemeral.calculator.creals

import java.math.BigInteger

/**
 * Computes the derivative of a function.
 * The function must be defined on the interval [`low`, `high`],
 * and the derivative must exist, and must be continuous and
 * monotone in the open interval [`low`, `high`].
 * The result is defined only in the open interval.
 */
fun monotoneDerivative(func: (Real) -> Real, low: Real, high: Real): (Real) -> Real {
    return MonotoneDerivativeUnaryRealFunction(func, low, high)
}

internal class MonotoneDerivativeUnaryRealFunction(
    private val func: (Real) -> Real,
    private val low: Real,
    private val high: Real,
) : (Real) -> Real {
    private val midHolder = (low + high).shiftRight(1)
    private val fLow = func(low)
    private val fMid = func(midHolder)
    private val fHigh = func(high)
    private val differenceMSD: Int
    private var derivative2MSD: Int

    // Rough approx. of msd of second
    // derivative.
    // This is increased to be an appr. bound
    // on the msd of |(f'(y)-f'(x))/(x-y)|
    // for any pair of points x and y
    // we have considered.
    // It may be better to keep a copy per
    // derivative value.
    init {
        val difference = high - low
        // compute approximate msd of
        // ((f_high - f_mid) - (f_mid - f_low))/(high - low)
        // This should be a very rough appr to the second derivative.
        // We add a little slop to err on the high side, since
        // a low estimate will cause extra iterations.
        val approximateDifference2 = fHigh - fMid.shiftLeft(1) + fLow
        differenceMSD = difference.msd()
        derivative2MSD = approximateDifference2.msd() - differenceMSD + 4
    }

    internal inner class MonotoneDerivativeReal(var arg: Real) : Real() {
        private var fArg: Real = func(arg)
        private var maxDeltaMSD: Int

        init {
            // The following must converge, since arg must be in the open interval.
            val leftDiff = arg - low
            val maxDeltaLeftMsd = leftDiff.msd()
            val rightDiff = high - arg
            val maxDeltaRightMsd = rightDiff.msd()
            if (leftDiff.signum() < 0 || rightDiff.signum() < 0) {
                throw ArithmeticException()
            }
            maxDeltaMSD = (if (maxDeltaLeftMsd < maxDeltaRightMsd) maxDeltaLeftMsd else maxDeltaRightMsd)
        }

        override fun approximate(precision: Int): BigInteger {
            val extraPrecision = 4
            var logDelta = precision - derivative2MSD
            // Ensure that we stay within the interval.
            if (logDelta > maxDeltaMSD) logDelta = maxDeltaMSD
            logDelta -= extraPrecision
            val delta = ONE.shiftLeft(logDelta)

            val left = arg - delta
            val right = arg + delta
            val fLeft = func(left)
            val fRight = func(right)
            val leftDerivative = (fArg - fLeft).shiftRight(logDelta)
            val rightDerivative = (fRight - fArg).shiftRight(logDelta)
            val evalPrecision = precision - extraPrecision
            val approximateLeftDerivative = leftDerivative.getApproximation(evalPrecision)
            val approximateRightDerivative = rightDerivative.getApproximation(evalPrecision)
            val derivativeDifference = approximateRightDerivative.subtract(approximateLeftDerivative).abs()
            if (derivativeDifference < BIG8) {
                return scale(approximateLeftDerivative, -extraPrecision)
            } else {
                checkForAbort()
                derivative2MSD = evalPrecision + derivativeDifference.bitLength() + 4 /*slop*/
                derivative2MSD -= logDelta
                return approximate(precision)
            }
        }
    }

    override fun invoke(argument: Real): Real {
        return MonotoneDerivativeReal(argument)
    }
}
