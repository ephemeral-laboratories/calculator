package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.creals.Real

/**
 * Result of attempting to parse a real.
 */
sealed class RealParseResult {
    /**
     * @property index the index we successfully processed up to.
     * @property parsedValue the [Real] parsed from the text.
     */
    data class Success(val index: Int, val parsedValue: Real) : RealParseResult()

    /**
     * @property index the index we successfully processed up to.
     * @property errorIndex the index of the first error.
     */
    data class Failure(val index: Int, val errorIndex: Int) : RealParseResult()
}
