package garden.ephemeral.calculator.creals

import garden.ephemeral.calculator.creals.impl.AddReal
import garden.ephemeral.calculator.creals.impl.AssumedIntReal
import garden.ephemeral.calculator.creals.impl.IntegerConstantReal
import garden.ephemeral.calculator.creals.impl.IntegralArctanReal
import garden.ephemeral.calculator.creals.impl.MultiplyReal
import garden.ephemeral.calculator.creals.impl.NegationReal
import garden.ephemeral.calculator.creals.impl.ReciprocalReal
import garden.ephemeral.calculator.creals.impl.ShiftedReal
import garden.ephemeral.calculator.creals.util.StringFloatRep
import garden.ephemeral.calculator.creals.util.minus
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.ln

/**
 * Constructive real numbers, also known as recursive, or computable reals.
 * Each recursive real number is represented as an object that provides an
 * approximation function for the real number.
 * The approximation function guarantees that the generated approximation
 * is accurate to the specified precision.
 * Arithmetic operations on constructive reals produce new such objects;
 * they typically do not perform any real computation.
 * In this sense, arithmetic computations are exact: They produce
 * a description which describes the exact answer, and can be used to
 * later approximate it to arbitrary precision.
 *
 * When approximations are generated, _e.g._ for output, they are
 * accurate to the requested precision; no cumulative rounding errors
 * are visible.
 * In order to achieve this precision, the approximation function will often
 * need to approximate subexpressions to greater precision than was originally
 * demanded.  Thus, the approximation of a constructive real number
 * generated through a complex sequence of operations may eventually require
 * evaluation to very high precision.  This usually makes such computations
 * prohibitively expensive for large numerical problems.
 * But it is perfectly appropriate for use in a desk calculator,
 * for small numerical problems, for the evaluation of expressions
 * computed by a symbolic algebra system, for testing of accuracy claims
 * for floating point code on small inputs, or the like.
 *
 * We expect that the vast majority of uses will ignore the particular
 * implementation, and the member functions [approximate]
 * and [getApproximation].  Such applications will treat [Real] as
 * a conventional numerical type, with an interface modelled on
 * `java.math.BigInteger`.  No subclasses of `Real`
 * will be explicitly mentioned by such a program.
 *
 * All standard arithmetic operations, as well as a few algebraic
 * and transcendental functions are provided.  Constructive reals are
 * immutable; thus all of these operations return a new constructive real.
 *
 * A few uses will require explicit construction of approximation functions.
 * This requires the construction of a subclass of `Real` with
 * an overridden `approximate` function.  Note that `approximate`
 * should only be defined, but never called.  `getApproximation`
 * provides the same functionality, but adds the caching necessary to obtain
 * reasonable performance.
 *
 * Any operation may throw [AbortedError] if the thread in
 * which it is executing is interrupted.  (`InterruptedException` cannot
 * be used for this purpose, since `Real` inherits from `Number`.)
 *
 * Any operation may also throw [PrecisionOverflowError]
 * if the precision request generated during any sub-calculation overflows
 * a 28-bit integer.  (This should be extremely unlikely, except as an
 * outcome of a division by zero, or other erroneous computation.)
 *
 * Real is the basic representation of a number.
 * Abstractly this is a function for computing an approximation plus the current best approximation.
 * We could do without the latter, but that would be atrociously slow.
 */
abstract class Real : Number() {
    /**
     * The smallest precision value with which [getApproximation] has been called.
     */
    protected var minPrecision: Int = 0

    /**
     * The scaled approximation corresponding to [minPrecision].
     * Updated by [getApproximation] / [calcApproximation].
     */
    protected var maxApproximation: BigInteger? = null

    /**
     * Tracks whether [maxApproximation] is valid. Original code used to store
     * an actual boolean here, but current code just checks whether `maxApproximation`
     * is `null`.
     */
    protected val isMaxApproximationValid: Boolean
        get() = maxApproximation != null

    /**
     * Must be defined in subclasses of [Real].
     *
     * Most users can ignore the existence of this method, and will
     * not ever need to define a [Real] subclass.
     *
     * @param precision the desired precision.
     *        Informally, `approximate(n)` gives a scaled approximation accurate to `2**n`.
     *        Implementations may safely assume that precision is at least a factor of 8 away from overflow.
     * @return `value / 2 ** precision` rounded to an integer.
     *         The error in the result is strictly `< 1`.
     */
    protected abstract fun approximate(precision: Int): BigInteger

    /**
     * Returns `value / 2 ** prec` rounded to an integer.
     *
     * The error in the result is strictly `< 1`.
     *
     * Produces the same answer as [approximate], but uses and
     * maintains a cached approximation.
     * Called only from `approximate` methods in subclasses.
     * Not needed if the provided operations on constructive reals suffice.
     */
    fun getApproximation(precision: Int): BigInteger {
        checkPrecision(precision)
        return if (isMaxApproximationValid && precision >= minPrecision) {
            scale(maxApproximation!!, minPrecision - precision)
        } else {
            calcApproximation(precision)
        }
    }

    /**
     * Calls [approximate] and then updates [minPrecision] and [maxApproximation]
     * with the calculation result.
     *
     * Function is open so that subclasses can choose to use a different
     * precision than requested, for performance reasons.
     */
    protected open fun calcApproximation(precision: Int): BigInteger {
        val result = approximate(precision)
        minPrecision = precision
        maxApproximation = result
        return result
    }

    /**
     * Returns the position of the MSD.
     *
     * If `x.msd() == n` then
     * `2**(n-1) < abs(x) < 2**(n+1)`
     *
     * This initial version assumes that [maxApproximation] is valid
     * and sufficiently removed from zero that the MSD is determined.
     */
    fun knownMSD(): Int {
        val firstDigit: Int
        val length = if (maxApproximation!!.signum() >= 0) {
            maxApproximation!!.bitLength()
        } else {
            maxApproximation!!.negate().bitLength()
        }
        firstDigit = minPrecision + length - 1
        return firstDigit
    }

    /**
     * This version may return `Integer.MIN_VALUE` if the correct answer is `< n`.
     */
    fun msd(n: Int): Int {
        if (!isMaxApproximationValid || maxApproximation!! in BIG_MINUS1..BIG1) {
            getApproximation(n - 1)
            if (maxApproximation!!.abs() <= BIG1) {
                // msd could still be arbitrarily far to the right.
                return Int.MIN_VALUE
            }
        }
        return knownMSD()
    }

    /**
     * Functionally equivalent to [msd], but iteratively evaluates to higher precision.
     */
    private fun iterMSD(n: Int): Int {
        var prec = 0

        while (prec > n + 30) {
            val msd = msd(prec)
            if (msd != Int.MIN_VALUE) return msd
            checkPrecision(prec)
            checkForAbort()
            prec = (prec * 3) / 2 - 16
        }
        return msd(n)
    }

    /**
     * This version returns a correct answer eventually, except
     * that it loops forever (or throws an exception when the
     * requested precision overflows) if this constructive real is zero.
     */
    fun msd(): Int {
        return iterMSD(Int.MIN_VALUE)
    }

    // Public operations.

    /**
     * Returns `0` if `x == y` to within the indicated tolerance,
     * `-1` if `x < y`, and `+1` if `x > y`.
     *
     * If `x` and `y` are indeed equal, it is guaranteed that `0` will be returned.
     * If they differ by less than the tolerance, anything may happen.
     * The tolerance allowed is the maximum of `(abs(this)+abs(other))*(2**relativeTolerance)`
     * and `2**absoluteTolerance`
     *
     * @param other the other constructive real.
     * @param relativeTolerance the relative tolerance in bits.
     * @param absoluteTolerance the absolute tolerance in bits.
     */
    fun compareTo(other: Real, relativeTolerance: Int, absoluteTolerance: Int): Int {
        val thisMSD = iterMSD(absoluteTolerance)
        val xMSD = other.iterMSD(if (thisMSD > absoluteTolerance) thisMSD else absoluteTolerance)
        val maxMSD = (if (xMSD > thisMSD) xMSD else thisMSD)
        val rel = maxMSD + relativeTolerance
        // This can't approach overflow, since `relativeTolerance` and `absoluteTolerance` are
        // effectively divided by 2, and MSDs are checked.
        val absolutePrecision = (if (rel > absoluteTolerance) rel else absoluteTolerance)
        return compareTo(other, absolutePrecision)
    }

    /**
     * Approximate comparison with only an absolute tolerance.
     *
     * Identical to the three argument version, but without a relative tolerance.
     * Result is 0 if both constructive reals are equal, indeterminate if they differ by less than
     * `2**absoluteTolerance`.
     *
     * @param other the other constructive real.
     * @param absoluteTolerance the absolute tolerance in bits.
     */
    fun compareTo(other: Real, absoluteTolerance: Int): Int {
        val neededPrecision = absoluteTolerance - 1
        val thisApproximation = getApproximation(neededPrecision)
        val xApproximation = other.getApproximation(neededPrecision)
        val comp1 = thisApproximation.compareTo(xApproximation + BIG1)
        if (comp1 > 0) return 1
        val comp2 = thisApproximation.compareTo(xApproximation - BIG1)
        if (comp2 < 0) return -1
        return 0
    }

    /**
     * Returns `-1` if `this &lt; x`, or `+1` if `this &gt; x`.
     *
     * Should be called only if `this != x`.
     * If `this == x`, this will not terminate correctly; typically it will run until it exhausts memory.
     * If the two constructive reals may be equal, the two or 3 argument version of [compareTo] should be used.
     */
    operator fun compareTo(other: Real): Int {
        var absoluteTolerance = -20
        while (true) {
            checkPrecision(absoluteTolerance)
            val result = compareTo(other, absoluteTolerance)
            if (result != 0) return result
            absoluteTolerance *= 2
        }
    }

    /**
     * Equivalent to `compareTo(Real.valueOf(0), a)`
     */
    fun signum(absoluteTolerance: Int): Int {
        if (isMaxApproximationValid) {
            val quickTry = maxApproximation!!.signum()
            if (quickTry != 0) return quickTry
        }
        val neededPrecision = absoluteTolerance - 1
        val thisApproximation = getApproximation(neededPrecision)
        return thisApproximation.signum()
    }

    /**
     * Return `-1` if negative, `+1` if positive.
     *
     * **Should be called only if `this != 0`!**
     *
     * In the `0` case, this will not terminate correctly; typically it
     * will run until it exhausts memory.
     * If the two constructive reals may be equal, the one or two argument
     * version of signum should be used.
     */
    fun signum(): Int {
        var a = -20
        while (true) {
            checkPrecision(a)
            val result = signum(a)
            if (result != 0) return result
            a *= 2
        }
    }

    /**
     * Gets a textual representation accurate to [pointsOfPrecision] places to the right
     * of the radix point.
     *
     * @param pointsOfPrecision number of digits (>= 0) included to the right of decimal point.
     * @param radix base (>= 2, <= 16) for the resulting representation.
     */
    fun toString(pointsOfPrecision: Int, radix: Int = 10): String {
        val scaledCR = if (16 == radix) {
            shiftLeft(4 * pointsOfPrecision)
        } else {
            val scaleFactor = radix.toBigInteger().pow(pointsOfPrecision)
            this * IntegerConstantReal(scaleFactor)
        }
        val scaledInt = scaledCR.getApproximation(0)
        var scaledString = scaledInt.abs().toString(radix)
        var result = if (0 == pointsOfPrecision) {
            scaledString
        } else {
            var len = scaledString.length
            if (len <= pointsOfPrecision) {
                // Add sufficient leading zeroes
                val z = zeroes(pointsOfPrecision + 1 - len)
                scaledString = z + scaledString
                len = pointsOfPrecision + 1
            }
            val whole = scaledString.substring(0, len - pointsOfPrecision)
            val fraction = scaledString.substring(len - pointsOfPrecision)
            "$whole.$fraction"
        }
        if (scaledInt.signum() < 0) {
            result = "-$result"
        }
        return result
    }

    /**
     * Equivalent to `toString(pointsOfPrecision = 10, radix = 10)`
     */
    override fun toString(): String {
        return toString(pointsOfPrecision = 10, radix = 10)
    }

    /**
     * Return a textual scientific notation representation accurate
     * to `pointsOfPrecision` places to the right of the decimal point.
     * `pointsOfPrecision` must be non-negative.  A value smaller than
     * `radix`**-`m` may be displayed as 0.
     * The `mantissa` component of the result is either "0"
     * or exactly `pointsOfPrecision` digits long.  The `sign`
     * component is zero exactly when the mantissa is "0".
     *
     * @param pointsOfPrecision Number of digits (&gt; 0) (in the specified radix)
     *        included to the right of decimal point.
     * @param radix Base (  2,  16) for the resulting representation.
     * @param msdPrecision Number of digits of precision (in the specified radix)
     *        used to distinguish number from zero.
     */
    fun toStringFloatRep(pointsOfPrecision: Int, radix: Int, msdPrecision: Int): StringFloatRep {
        if (pointsOfPrecision <= 0) throw ArithmeticException()
        val log2Radix = ln(radix.toDouble()) / doubleLog2
        val bigRadix = radix.toBigInteger()
        val longMSDPrecisionBits = (log2Radix * msdPrecision.toDouble()).toLong()
        if (longMSDPrecisionBits > Int.MAX_VALUE.toLong() || longMSDPrecisionBits < Int.MIN_VALUE.toLong()) throw PrecisionOverflowError()
        val msdPrecisionBits = longMSDPrecisionBits.toInt()
        checkPrecision(msdPrecisionBits)
        val msd = iterMSD(msdPrecisionBits - 2)
        if (msd == Int.MIN_VALUE) return StringFloatRep(0, listOf(0), radix, 0)
        var exponent = ceil(msd.toDouble() / log2Radix).toInt()
        // Guess for the exponent.  Try to get it usually right.
        val scaleExponent = exponent - pointsOfPrecision
        val scale = if (scaleExponent > 0) {
            valueOf(bigRadix.pow(scaleExponent)).reciprocal()
        } else {
            valueOf(bigRadix.pow(-scaleExponent))
        }
        var scaledRes = times(scale)
        var scaledInt = scaledRes.getApproximation(0)
        var sign = scaledInt.signum()

        var scaledMantissa = intToDigits(scaledInt.abs(), radix)
        while (scaledMantissa.size < pointsOfPrecision) {
            // exponent was too large.  Adjust.
            scaledRes *= valueOf(bigRadix)
            exponent -= 1
            scaledInt = scaledRes.getApproximation(0)
            sign = scaledInt.signum()
            scaledMantissa = intToDigits(scaledInt, radix)
        }
        if (scaledMantissa.size > pointsOfPrecision) {
            // exponent was too small.  Adjust by truncating.
            exponent += (scaledMantissa.size - pointsOfPrecision)
            scaledMantissa = scaledMantissa.subList(0, pointsOfPrecision)
        }
        return StringFloatRep(sign, scaledMantissa, radix, exponent)
    }

    private fun intToDigits(value: BigInteger, radix: Int): List<Int> = buildList {
        if (value == BigInteger.ZERO) return listOf(0)
        var remaining = value
        val bigRadix = radix.toBigInteger()
        while (remaining > BigInteger.ZERO) {
            val (newRemaining, digit) = remaining.divideAndRemainder(bigRadix)
            remaining = newRemaining
            add(digit.toInt())
        }
        reverse()
    }

    /**
     * Return a BigInteger which differs by less than one from the
     * constructive real.
     */
    fun toBigInteger(): BigInteger {
        return getApproximation(0)
    }

    override fun toInt(): Int {
        return toBigInteger().toInt()
    }

    override fun toShort(): Short {
        return toBigInteger().toShort()
    }

    override fun toByte(): Byte {
        return toBigInteger().toByte()
    }

    override fun toLong(): Long {
        return toBigInteger().toLong()
    }

    /**
     * Return a double which differs by less than one in the least
     * represented bit from the constructive real.
     */
    override fun toDouble(): Double {
        val myMSD = iterMSD(-1080 /* slightly > exp. range */)
        if (Int.MIN_VALUE == myMSD) return 0.0
        val neededPrecision = myMSD - 60
        val scaledInt = getApproximation(neededPrecision).toDouble()
        val mayUnderflow = (neededPrecision < -1000)
        var scaledIntRep = java.lang.Double.doubleToLongBits(scaledInt)
        val expAdj = (if (mayUnderflow) neededPrecision + 96 else neededPrecision).toLong()
        val origExp = (scaledIntRep shr 52) and 0x7ffL
        if (((origExp + expAdj) and 0x7ffL.inv()) != 0L) {
            // overflow
            return if (scaledInt < 0.0) {
                Double.NEGATIVE_INFINITY
            } else {
                Double.POSITIVE_INFINITY
            }
        }
        scaledIntRep += expAdj shl 52
        val result = java.lang.Double.longBitsToDouble(scaledIntRep)
        if (mayUnderflow) {
            val two48 = (1 shl 48).toDouble()
            return result / two48 / two48
        } else {
            return result
        }
    }

    /**
     * Return a float which differs by less than one in the least
     * represented bit from the constructive real.
     */
    override fun toFloat(): Float {
        return toDouble().toFloat()
    }

    /**
     * Add two constructive reals.
     */
    operator fun plus(x: Real): Real {
        return AddReal(this, x)
    }

    /**
     * Multiply a constructive real by 2**n.
     * @param n    shift count, may be negative
     */
    fun shiftLeft(n: Int): Real {
        checkPrecision(n)
        return ShiftedReal(this, n)
    }

    /**
     * Multiply a constructive real by 2**(-n).
     * @param n    shift count, may be negative
     */
    fun shiftRight(n: Int): Real {
        checkPrecision(n)
        return ShiftedReal(this, -n)
    }

    /**
     * Produce a constructive real equivalent to the original, assuming
     * the original was an integer.  Undefined results if the original
     * was not an integer.  Prevents evaluation of digits to the right
     * of the decimal point, and may thus improve performance.
     */
    fun assumeInt(): Real {
        return AssumedIntReal(this)
    }

    /**
     * The additive inverse of a constructive real
     */
    operator fun unaryMinus(): Real = NegationReal(this)

    /**
     * Returns `this`.
     */
    operator fun unaryPlus() = this

    /**
     * The difference between two constructive reals
     */
    operator fun minus(x: Real): Real {
        return AddReal(this, -x)
    }

    /**
     * The product of two constructive reals
     */
    operator fun times(x: Real): Real {
        return MultiplyReal(this, x)
    }

    /**
     * The multiplicative inverse of a constructive real.
     *
     * `x.inverse()` is equivalent to `Real.valueOf(1) / x`.
     */
    fun reciprocal(): Real {
        return ReciprocalReal(this)
    }

    /**
     * The quotient of two constructive reals.
     */
    operator fun div(x: Real): Real {
        return MultiplyReal(this, x.reciprocal())
    }

    /**
     * Gets this value raised to the power of the provided exponent.
     *
     * @param exponent the exponent.
     * @return `this ** exponent`.
     */
    fun pow(exponent: Real) = exp(ln(this) * exponent)

    companion object {
        // First some frequently used constants, so we don't have to
        // recompute these all over the place.

        val BIG0: BigInteger = BigInteger.ZERO
        val BIG1: BigInteger = BigInteger.ONE
        val BIG_MINUS1 = (-1).toBigInteger()
        val BIG2: BigInteger = BigInteger.TWO
        val BIG3 = 3.toBigInteger()
        val BIG6 = 6.toBigInteger()
        val BIG8 = 8.toBigInteger()
        val BIG10: BigInteger = BigInteger.TEN

        val ZERO = valueOf(0)
        val ONE = valueOf(1)
        val MINUS_ONE = valueOf(-1)
        val TWO = valueOf(2)
        val ONE_HALF = valueOf(0.5)
        val FOUR = valueOf(4)

        /**
         * The ratio of a circle's circumference to its diameter.
         */
        val PI: Real = FOUR * (FOUR * arctanReciprocal(5) - arctanReciprocal(239))

        /**
         * The ratio of a circle's circumference to its radius.
         */
        val TAU = PI.shiftLeft(1)

        // pi/4 = 4*atan(1/5) - atan(1/239)
        val HALF_PI: Real = PI.shiftRight(1)

        private val doubleLog2: Double = ln(2.0)

        private var tenNinths: Real = valueOf(10) / valueOf(9)
        private var twentyFiveTwentyFourths: Real = valueOf(25) / valueOf(24)
        private var eightyOneEightyeths: Real = valueOf(81) / valueOf(80)
        private var ln2_1: Real = valueOf(7) * simpleLn(tenNinths)
        private var ln2_2: Real = valueOf(2) * simpleLn(twentyFiveTwentyFourths)
        private var ln2_3: Real = valueOf(3) * simpleLn(eightyOneEightyeths)
        internal var ln2: Real = ln2_1 - ln2_2 + ln2_3

        /**
         * Setting this to true requests that  all computations be aborted by
         * throwing AbortedError.  Must be rest to false before any further
         * computation.  Ideally Thread.interrupt() should be used instead, but
         * that doesn't appear to be consistently supported by browser VMs.
         */
        @JvmField
        @Volatile
        var pleaseStop: Boolean = false

        /**
         * Convenience method for subclasses to call to check for an abort of the calculation.
         * Tight calculation loops don't check for the thread being interrupted automatically.
         */
        @Throws(AbortedError::class)
        @JvmStatic
        protected fun checkForAbort() {
            if (Thread.interrupted() || pleaseStop) throw AbortedError()
        }

        // min_prec and max_val are valid.
        // Helper functions
        fun boundLog2(n: Int): Int {
            val abs_n = abs(n.toDouble()).toInt()
            return ceil(ln((abs_n + 1).toDouble()) / ln(2.0)).toInt()
        }

        // Check that a precision is at least a factor of 8 away from
        // overflowing the integer used to hold a precision spec.
        // We generally perform this check early on, and then convince
        // ourselves that none of the operations performed on precisions
        // inside a function can generate an overflow.
        fun checkPrecision(n: Int) {
            val high = n shr 28
            // if n is not in danger of overflowing, then the 4 high order
            // bits should be identical.  Thus high is either 0 or -1.
            // The rest of this is to test for either of those in a way
            // that should be as cheap as possible.
            val highShifted = n shr 29
            if ((high xor highShifted) != 0) {
                throw PrecisionOverflowError()
            }
        }

        /**
         * The constructive real number corresponding to a `BigInteger`.
         */
        fun valueOf(n: BigInteger): Real = IntegerConstantReal(n)

        /**
         * The constructive real number corresponding to an `Int`.
         */
        fun valueOf(n: Int) = valueOf(n.toBigInteger())

        /**
         * The constructive real number corresponding to a `Long`.
         */
        fun valueOf(n: Long) = valueOf(n.toBigInteger())

        /**
         * The constructive real number corresponding to a
         * Java `double`.
         * The result is undefined if argument is infinite or NaN.
         */
        fun valueOf(n: Double): Real {
            if (java.lang.Double.isNaN(n)) throw ArithmeticException()
            if (java.lang.Double.isInfinite(n)) throw ArithmeticException()
            val negative = (n < 0.0)
            val bits = java.lang.Double.doubleToLongBits(abs(n))
            var mantissa = (bits and 0xfffffffffffffL)
            val biasedExponent = (bits shr 52).toInt()
            val exponent = biasedExponent - 1075
            if (biasedExponent != 0) {
                mantissa += (1L shl 52)
            } else {
                mantissa = mantissa shl 1
            }
            var result = valueOf(mantissa).shiftLeft(exponent)
            if (negative) result = -result
            return result
        }

        /**
         * The constructive real number corresponding to a
         * Java `float`.
         * The result is undefined if argument is infinite or NaN.
         */
        fun valueOf(n: Float): Real {
            return valueOf(n.toDouble())
        }

        // Multiply k by 2**n.
        fun shift(k: BigInteger, n: Int): BigInteger {
            if (n == 0) return k
            if (n < 0) return k.shiftRight(-n)
            return k.shiftLeft(n)
        }

        // Multiply by 2**n, rounding result
        fun scale(k: BigInteger, n: Int): BigInteger {
            if (n >= 0) {
                return k.shiftLeft(n)
            } else {
                val adjK = shift(k, n + 1) + BIG1
                return adjK.shiftRight(1)
            }
        }

        // A helper function for toString.
        // Generate a String containing n zeroes.
        private fun zeroes(n: Int): String {
            val a = CharArray(n)
            for (i in 0 until n) {
                a[i] = '0'
            }
            return String(a)
        }

        // Atan of integer reciprocal.  Used for PI.  Could perhaps
        // be made public.
        private fun arctanReciprocal(n: Int): Real {
            return IntegralArctanReal(n)
        }

        /**
         * Return the constructive real number corresponding to the given
         * textual representation and radix.
         *
         * @param s    [-] digit* [. digit*]
         * @param radix
         */
        @Throws(NumberFormatException::class)
        fun valueOf(s: String, radix: Int = 10): Real {
            var len = s.length
            var startPos = 0
            val fraction: String
            while (s[startPos] == ' ') ++startPos
            while (s[len - 1] == ' ') --len
            var pointPos = s.indexOf('.', startPos)
            if (pointPos == -1) {
                pointPos = len
                fraction = "0"
            } else {
                fraction = s.substring(pointPos + 1, len)
            }
            val whole = s.substring(startPos, pointPos)
            val scaledResult = (whole + fraction).toBigInteger(radix)
            val divisor = radix.toBigInteger().pow(fraction.length)
            return valueOf(scaledResult) / valueOf(divisor)
        }

        // sixteenths, i.e. 1/2
        val lowLnLimit: BigInteger = BIG8

        // 1.5
        val highLnLimit: BigInteger = (16 + 8).toBigInteger()

        val scaled4: BigInteger = (4 * 16).toBigInteger()
    }
}
