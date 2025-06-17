package garden.ephemeral.calculator

import garden.ephemeral.calculator.logging.KermitLogger

/**
 * Main accessor for code wishing to log.
 */
val Logger = KermitLogger.withSubTag("garden.ephemeral.calculator")
