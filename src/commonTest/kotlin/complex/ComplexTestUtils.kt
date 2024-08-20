package garden.ephemeral.calculator.complex

import garden.ephemeral.calculator.creals.Real

val Int.i get() = Real.valueOf(this).i
val Double.i get() = Real.valueOf(this).i
val Real.i get() = Complex(Real.ZERO, this)

operator fun Int.plus(w: Complex) = Real.valueOf(this) + w
operator fun Int.minus(w: Complex) = Real.valueOf(this) - w
operator fun Double.plus(w: Complex) = Real.valueOf(this) + w
operator fun Double.minus(w: Complex) = Real.valueOf(this) - w
operator fun Real.plus(w: Complex) = toComplex() + w
operator fun Real.minus(w: Complex) = toComplex() - w
