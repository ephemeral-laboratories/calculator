package garden.ephemeral.calculator.ui

import androidx.compose.runtime.Composable
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.theme_dark
import garden.ephemeral.calculator.calculator.generated.resources.theme_light
import garden.ephemeral.calculator.calculator.generated.resources.theme_system_default
import garden.ephemeral.calculator.ui.common.Localizable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class ThemeOption(private val stringResourceKey: StringResource) : Localizable {
    SYSTEM_DEFAULT(Res.string.theme_system_default),
    LIGHT(Res.string.theme_light),
    DARK(Res.string.theme_dark),
    ;

    override val localizedName: String
        @Composable
        get() = stringResource(stringResourceKey)
}
