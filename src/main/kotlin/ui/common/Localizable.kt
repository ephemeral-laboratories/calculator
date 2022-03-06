package garden.ephemeral.calculator.ui.common

/**
 * Interface for objects which have a localised name.
 *
 * Because this is UI code, we can assume the default locale is the only
 * locale we need to worry about. A common version of the same class would
 * need to accept the locale somehow.
 */
interface Localizable {
    val localizedName: String
}
