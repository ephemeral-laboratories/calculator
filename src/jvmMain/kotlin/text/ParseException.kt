package garden.ephemeral.calculator.text

/**
 * Replacement for `java.text.ParseException` to remove our code's reliance on
 * `java.text`, for the sake of portability.
 *
 * Our version also gets to have a slightly better API, because we control nullability
 * of the parameters.
 *
 * @param message the detail message
 * @param errorOffset the position where the error is found while parsing.
 * @param cause the underlying cause for the error, if there was one, `null` otherwise.
 * @property errorOffset the zero-based character offset into the string being parsed
 *           at which the error was found during parsing.
 */
class ParseException(
    message: String,
    val errorOffset: Int,
    cause: Throwable? = null
) : Exception(message, cause)
