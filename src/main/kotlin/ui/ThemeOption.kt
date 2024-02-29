package garden.ephemeral.calculator.ui

import garden.ephemeral.calculator.ui.common.Localizable

enum class ThemeOption(override val localizedName: String) : Localizable {
    SYSTEM_DEFAULT(AppStrings.SystemDefault),
    LIGHT(AppStrings.Light),
    DARK(AppStrings.Dark),
}
