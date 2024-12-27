package garden.ephemeral.calculator.values

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.complex.toComplex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.abs

/**
 * Pseudo-number container class holding either a real or a complex number.
 */
sealed class Value {

    /**
     * The contained value.
     */
    abstract val value: Any

    /**
     * The contained value, forcibly converted to complex.
     */
    abstract val complexValue: Complex

    /**
     * Compares this value for proximity to another value.
     *
     * Note that this method is not smart regarding real vs. complex values.
     * It will simply return `false` if the two values aren't the same type.
     * This could be improved if it became a problem in the future.
     *
     * @param other the other value.
     * @param delta the acceptable delta before the values are considered different.
     * @return `true` if the values are close, `false` otherwise.
     */
    abstract fun isCloseTo(other: Value, delta: Double): Boolean

    class OfReal internal constructor(override val value: Real) : Value() {
        override val complexValue: Complex
            get() = value.toComplex()

        override fun isCloseTo(other: Value, delta: Double): Boolean {
            require(other is OfReal) { "No way to compare $this (${javaClass}) with $other (${other.javaClass})" }
            return abs(value - other.value).toDouble() < delta
        }
    }

    class OfComplex internal constructor(override val value: Complex) : Value() {
        override val complexValue: Complex
            get() = value

        override fun isCloseTo(other: Value, delta: Double): Boolean {
            require(other is OfComplex) { "No way to compare $this (${javaClass}) with $other (${other.javaClass})" }
            return (value - other.value).norm.toDouble() < delta
        }
    }
}

fun Value(value: Real): Value = Value.OfReal(value)
fun Value(value: Complex): Value = Value.OfComplex(value)
