package garden.ephemeral.calculator.ui

object AppStrings {
    // TODO: i18n
    val InputError get() = "Input error"
    val Settings get() = "Settings"

    val NumberFormat get() = "Number format"
    val Decimal get() = "Decimal"
    val Dozenal get() = "Dozenal"

    val RadixSeparator get() = "Radix separator"
    val Period get() = "Period (.)"
    val Comma get() = "Comma (,)"
    val Semicolon get() = "Semicolon (;)"
}

// XXX: Could attach to here to gain access via LocalLocalization.current.calculator
//      but not yet sure whether this is the right way to do it and the API is
//      still experimental.
// val PlatformLocalization.calculator get() = AppStrings
