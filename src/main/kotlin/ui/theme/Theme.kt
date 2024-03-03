package garden.ephemeral.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import garden.ephemeral.calculator.ui.ThemeOption

@Composable
fun AppTheme(option: ThemeOption, content: @Composable () -> Unit) {
    // dynamicLightColorScheme() and dynamicDarkColorScheme() are missing from Multiplatform still.
    // I'd prefer to inherit colours from the user's desktop theme! :(
    val colorScheme = when (option) {
        ThemeOption.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) DarkColors else LightColors
        ThemeOption.DARK -> DarkColors
        ThemeOption.LIGHT -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content,
    )
}
