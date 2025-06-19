package garden.ephemeral.calculator.creals

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.bigint.toBigInt

private class MonotoneDerivativeReal(
    private val func: (Real) -> Real,
    private val arg: Real,
    low: Real,
    high: Real,
    private var derivative2MSD: Int,
) : Real() {
    private val fArg: Real = func(arg)
    private val maxDeltaMSD: Int

    init {
        // The following must converge, since arg must be in the open interval.
        val leftDiff = arg - low
        val maxDeltaLeftMsd = leftDiff.msd()
        val rightDiff = high - arg
        val maxDeltaRightMsd = rightDiff.msd()
        if (leftDiff.signum() < 0 || rightDiff.signum() < 0) {
            throw ArithmeticException()
        }
        maxDeltaMSD = if (maxDeltaLeftMsd < maxDeltaRightMsd) maxDeltaLeftMsd else maxDeltaRightMsd
    }

    override fun approximate(precision: Int): BigInt {
        val extraPrecision = 4
        // Ensure that we stay within the interval.
        val logDelta = (precision - derivative2MSD).coerceAtMost(maxDeltaMSD) - extraPrecision
        val delta = ONE shl logDelta

        val left = arg - delta
        val right = arg + delta
        val fLeft = func(left)
        val fRight = func(right)
        val leftDerivative = (fArg - fLeft) shr logDelta
        val rightDerivative = (fRight - fArg) shr logDelta
        val evalPrecision = precision - extraPrecision
        val approximateLeftDerivative = leftDerivative.getApproximation(evalPrecision)
        val approximateRightDerivative = rightDerivative.getApproximation(evalPrecision)
        val derivativeDifference = (approximateRightDerivative - approximateLeftDerivative).abs()
        if (derivativeDifference < 8.toBigInt()) {
            return approximateLeftDerivative.scale(-extraPrecision)
        } else {
            checkForAbort()
            derivative2MSD = evalPrecision + derivativeDifference.bitLength() + 4 /*slop*/
            derivative2MSD -= logDelta
            return approximate(precision)
        }
    }
}

/**
 * Computes the derivative of a function.
 * The function must be defined on the interval [`low`, `high`],
 * and the derivative must exist, and must be continuous and
 * monotone in the open interval [`low`, `high`].
 * The result is defined only in the open interval.
 */
fun monotoneDerivative(func: (Real) -> Real, low: Real, high: Real): (Real) -> Real {
    val midHolder = (low + high).shr(1)
    val fLow = func(low)
    val fMid = func(midHolder)
    val fHigh = func(high)

    // Rough approx. of msd of second
    // derivative.
    // This is increased to be an appr. bound
    // on the msd of |(f'(y)-f'(x))/(x-y)|
    // for any pair of points x and y
    // we have considered.
    // It may be better to keep a copy per
    // derivative value.
    val difference = high - low
    // compute approximate msd of
    // ((f_high - f_mid) - (f_mid - f_low))/(high - low)
    // This should be a very rough appr to the second derivative.
    // We add a little slop to err on the high side, since
    // a low estimate will cause extra iterations.
    val approximateDifference2 = fHigh - (fMid shl 1) + fLow
    val differenceMSD: Int = difference.msd()
    val derivative2MSD: Int = approximateDifference2.msd() - differenceMSD + 4

    return { argument ->
        MonotoneDerivativeReal(
            func = func,
            arg = argument,
            low = low,
            high = high,
            derivative2MSD = derivative2MSD,
        )
    }
}
