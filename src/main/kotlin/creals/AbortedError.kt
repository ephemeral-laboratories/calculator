package garden.ephemeral.calculator.creals

/**
 * Indicates a constructive real operation was interrupted.
 * Most constructive real operations may throw such an error.
 * This is an error, since Number methods may not raise such
 * exceptions.
 */
class AbortedError(message: String? = null) : Error(message)
