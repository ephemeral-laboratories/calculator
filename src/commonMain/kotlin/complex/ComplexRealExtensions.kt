package garden.ephemeral.calculator.complex

import garden.ephemeral.calculator.creals.Real

/**
 * Converts a [Real] to a [Complex].
 */
fun Real.toComplex() = Complex(this, Real.ZERO)
