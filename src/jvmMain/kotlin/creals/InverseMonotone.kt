package garden.ephemeral.calculator.creals

import garden.ephemeral.calculator.bigint.BigInt
import garden.ephemeral.calculator.bigint.toBigInt

private class InverseIncreasingReal(
    private val func: (Real) -> Real,
    private val low: Real,
    private val high: Real,
    private val arg: Real,
    private val fLow: Real,
    private val fHigh: Real,
    private val maxMSD: Int,
    private val maxArgPrecision: Int,
    private val derivMSD: Int,
) : Real() {

    // Comparison with a difference of one treated as equality.
    private fun sloppyCompare(x: BigInt, y: BigInt): Int {
        val difference = x - y
        if (difference > BigInt.One) {
            return 1
        }
        if (difference < BigInt.MinusOne) {
            return -1
        }
        return 0
    }

    override fun approximate(precision: Int): BigInt {
        val extraArgPrecision = 4
        val fn = func
        var smallSteps = 0 // Number of preceding ineffective
        // steps.  If this number gets >= 2,
        // we perform a binary search step
        // to ensure forward progress.
        val digitsNeeded = maxMSD - precision
        if (digitsNeeded < 0) return BigInt.Zero
        var workingArgPrecision = precision - extraArgPrecision
        if (workingArgPrecision > maxArgPrecision) {
            workingArgPrecision = maxArgPrecision
        }
        var workingEvalPrecision = workingArgPrecision + derivMSD - 20
        // initial guess
        // We use a combination of binary search and something like
        // the secant method.  This always converges linearly,
        // and should converge quadratically for well-behaved
        // functions.
        // F_l and f_h are always the approximate images of l and h.
        // At any point, arg is between f_l and f_h, or no more than
        // one outside [f_l, f_h].
        // L and h are implicitly scaled by working_arg_prec.
        // The scaled values of l and h are strictly between low and high.
        // If at_left is true, then l is logically at the left
        // end of the interval.  We approximate this by setting l to
        // a point slightly inside the interval, and letting f_l
        // approximate the function value at the endpoint.
        // If at_right is true, r and f_r are set correspondingly.
        // At the endpoints of the interval, f_l and f_h may correspond
        // to the endpoints, even if l and h are slightly inside.
        // F_l and f_u are scaled by working_eval_prec.
        // Working_eval_prec may need to be adjusted depending
        // on the derivative of f.
        var atLeft: Boolean
        var atRight: Boolean
        var l: BigInt
        var fL: BigInt
        var h: BigInt
        var fH: BigInt
        val lowApproximation = low.getApproximation(workingArgPrecision) + BigInt.One
        val highApproximation = high.getApproximation(workingArgPrecision) - BigInt.One
        var argApproximation = arg.getApproximation(workingEvalPrecision)
        val haveGoodApproximation = (isMaxApproximationValid && minPrecision < maxMSD)
        if (digitsNeeded < 30 && !haveGoodApproximation) {
            Logger.debug { "Setting interval to entire domain" }
            h = highApproximation
            fH = fHigh.getApproximation(workingEvalPrecision)
            l = lowApproximation
            fL = fLow.getApproximation(workingEvalPrecision)
            // Check for clear out-of-bounds case.
            // Close cases may fail in other ways.
            if (fH < argApproximation - BigInt.One || fL > argApproximation + BigInt.One) {
                throw ArithmeticException()
            }
            atLeft = true
            atRight = true
            smallSteps = 2 // Start with bin search step.
        } else {
            var roughPrecision = precision + digitsNeeded / 2

            if (haveGoodApproximation && ((digitsNeeded < 30) || (minPrecision < (precision + ((3 * digitsNeeded) / 4))))) {
                roughPrecision = minPrecision
            }
            val roughApproximation = getApproximation(roughPrecision)
            Logger.debug {
                "Setting interval based on prev. appr\n" +
                        "prev. prec = $roughPrecision appr = $roughApproximation"
            }
            h = (roughApproximation + BigInt.One) shl (roughPrecision - workingArgPrecision)
            l = (roughApproximation - BigInt.One) shl (roughPrecision - workingArgPrecision)
            if (h > highApproximation) {
                h = highApproximation
                fH = fHigh.getApproximation(workingEvalPrecision)
                atRight = true
            } else {
                val hCR = valueOf(h) shl workingArgPrecision
                fH = fn(hCR).getApproximation(workingEvalPrecision)
                atRight = false
            }
            if (l < lowApproximation) {
                l = lowApproximation
                fL = fLow.getApproximation(workingEvalPrecision)
                atLeft = true
            } else {
                val lCR = valueOf(l) shl workingArgPrecision
                fL = fn(lCR).getApproximation(workingEvalPrecision)
                atLeft = false
            }
        }
        var difference = h - l
        var i = 0
        while (true) {
            checkForAbort()
            Logger.debug {
                "***Iteration: $i" +
                        "Arg prec = $workingArgPrecision eval prec = $workingEvalPrecision arg appr. = $argApproximation" +
                        "l = $l; h = $h" +
                        "f(l) = $fL; f(h) = $fH"
            }
            if (difference < 6.toBigInt()) {
                // Answer is less than 1/2 ulp away from h.
                return h.scale(-extraArgPrecision)
            }
            val fDifference = fH - fL
            // Narrow the interval by dividing at a cleverly
            // chosen point (guess) in the middle.
            run {
                var guess: BigInt
                if (smallSteps >= 2 || fDifference.signum() == 0) {
                    // Do a binary search step to guarantee linear
                    // convergence.
                    guess = (l + h) shr 1
                } else {
                    // interpolate.
                    // f_difference is nonzero here.
                    val argDifference = argApproximation - fL
                    val t = argDifference * difference
                    var adj = t / fDifference
                    if (adj < (difference shr 2)) {
                        // Very close to left side of interval;
                        // move closer to center.
                        // If one of the endpoints is very close to
                        // the answer, this slows conversion a bit.
                        // But it greatly increases the probability
                        // that the answer will be in the smaller
                        // subinterval.
                        adj = adj shl 1
                    } else if (adj > ((difference * 3.toBigInt()) shr 2)) {
                        adj = difference - ((difference - adj) shl 1)
                    }
                    if (adj.signum() <= 0) adj = 2.toBigInt()
                    if (adj >= difference) adj = difference - 2.toBigInt()
                    guess = (if (adj.signum() <= 0) l + 2.toBigInt() else l + adj)
                }
                var outcome: Int
                var tweak = 2.toBigInt()
                var fGuess: BigInt
                var adjustPrecision = false
                while (true) {
                    val guessCr = valueOf(guess) shl workingArgPrecision
                    Logger.debug { "Evaluating at $guessCr with precision $workingEvalPrecision" }
                    val fGuessCr = fn(guessCr)
                    Logger.debug { "fn value = $fGuessCr" }
                    fGuess = fGuessCr.getApproximation(workingEvalPrecision)
                    outcome = sloppyCompare(fGuess, argApproximation)
                    if (outcome != 0) break
                    // Alternately increase evaluation precision
                    // and adjust guess slightly.
                    // This should be an unlikely case.
                    if (adjustPrecision) {
                        // adjust working_eval_prec to get enough resolution.
                        val adjustment = if (derivMSD > 0) -20 else derivMSD - 20
                        val lCr = valueOf(l) shl workingArgPrecision
                        val hCr = valueOf(h) shl workingArgPrecision
                        workingEvalPrecision += adjustment
                        Logger.debug { "New eval prec = $workingEvalPrecision${if (atLeft) "(at left)" else ""}${if (atRight) "(at right)" else ""}" }
                        fL = if (atLeft) {
                            fLow.getApproximation(workingEvalPrecision)
                        } else {
                            fn(lCr).getApproximation(workingEvalPrecision)
                        }
                        fH = if (atRight) {
                            fHigh.getApproximation(workingEvalPrecision)
                        } else {
                            fn(hCr).getApproximation(workingEvalPrecision)
                        }
                        argApproximation = arg.getApproximation(workingEvalPrecision)
                    } else {
                        // guess might be exactly right; tweak it slightly.
                        Logger.debug { "tweaking guess" }
                        val newGuess = guess + tweak
                        guess = if (newGuess >= h) {
                            guess - tweak
                        } else {
                            newGuess
                        }
                        // If we keep hitting the right answer, it's
                        // important to alternate which side we move it
                        // to, so that the interval shrinks rapidly.
                        tweak = -tweak
                    }
                    adjustPrecision = !adjustPrecision
                }
                if (outcome > 0) {
                    h = guess
                    fH = fGuess
                    atRight = false
                } else {
                    l = guess
                    fL = fGuess
                    atLeft = false
                }
                val newDifference = h - l
                if (newDifference >= (difference shr 1)) {
                    smallSteps++
                } else {
                    smallSteps = 0
                }
                difference = newDifference
            }
            i++
        }
    }
}


/**
 * Computes the inverse of this function, which must be defined
 * and strictly monotone on the interval [`low`, `high`].
 * The resulting function is defined only on the image of
 * [`low`, `high`].
 * The original function may be either increasing or decreasing.
 */
fun inverseMonotone(func: (Real) -> Real, low: Real, high: Real): (Real) -> Real {
    // Rough approx. of MSD of first derivative.
    val fLow = func(low)
    val fHigh = func(high)

    val funcIsNegated: Boolean
    val maybeNegatedFunc: (Real) -> Real
    val maybeNegatedFLow: Real
    val maybeNegatedFHigh: Real

    // Since func is monotone and low < high, the following test converges.
    if (fLow > fHigh) {
        funcIsNegated = true
        maybeNegatedFunc = { x -> func(-x) }
        maybeNegatedFLow = -fLow
        maybeNegatedFHigh = -fHigh
    } else {
        funcIsNegated = false
        maybeNegatedFunc = func
        maybeNegatedFLow = fLow
        maybeNegatedFHigh = fHigh
    }

    val maxMSD: Int = max(abs(low), abs(high)).msd()

    // Bound on msd of both f(high) and f(low)
    val maxArgPrecision: Int = (high - low).msd() - 4

    // base**max_arg_prec is a small fraction of low - high.
    val derivMSD: Int = ((maybeNegatedFHigh - maybeNegatedFLow) / (high - low)).msd()

    return { argument ->
        val maybeNegatedArg = if (funcIsNegated) -argument else argument
        InverseIncreasingReal(
            func = maybeNegatedFunc,
            low = low,
            high = high,
            arg = maybeNegatedArg,
            fLow = maybeNegatedFLow,
            fHigh = maybeNegatedFHigh,
            maxMSD = maxMSD,
            maxArgPrecision = maxArgPrecision,
            derivMSD = derivMSD,
        )
    }
}
