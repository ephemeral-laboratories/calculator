package garden.ephemeral.calculator.creals

/**
 * Indicates that the number of bits of precision requested by
 * a computation on constructive reals required more than 28 bits,
 * and was thus in danger of overflowing an int.
 * This is likely to be a symptom of a diverging computation,
 * _e.g._ division by zero.
 */
class PrecisionOverflowError(message: String? = null) : Error(message)
