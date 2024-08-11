package garden.ephemeral.calculator.creals.impl

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.util.scale
import java.math.BigInteger

/**
 * Representation of the product of 2 constructive reals.
 */
internal class MultiplyReal(private var op1: Real, private var op2: Real) : Real() {
    override fun approximate(precision: Int): BigInteger {
        val halfPrecision = (precision shr 1) - 1
        var msdOp1 = op1.msd(halfPrecision)
        var msdOp2: Int

        if (msdOp1 == Int.MIN_VALUE) {
            msdOp2 = op2.msd(halfPrecision)
            if (msdOp2 == Int.MIN_VALUE) {
                // Product is small enough that zero will do as an approximation.
                return BigInteger.ZERO
            } else {
                // Swap them, so the larger operand (in absolute value) is first.
                val tmp = op1
                op1 = op2
                op2 = tmp
                msdOp1 = msdOp2
            }
        }
        // msd_op1 is valid at this point.
        val prec2 = precision - msdOp1 - 3 // Precision needed for op2.
        // The appr. error is multiplied by at most
        // 2 ** (msd_op1 + 1)
        // Thus each approximation contributes 1/4 ulp
        // to the rounding error, and the final rounding adds
        // another 1/2 ulp.
        val approx2 = op2.getApproximation(prec2)
        if (approx2.signum() == 0) return BigInteger.ZERO
        msdOp2 = op2.knownMSD()
        val prec1 = precision - msdOp2 - 3 // Precision needed for op1.
        val approx1 = op1.getApproximation(prec1)
        val scaleDigits = prec1 + prec2 - precision
        return (approx1 * approx2).scale(scaleDigits)
    }
}
