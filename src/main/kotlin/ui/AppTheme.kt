package garden.ephemeral.calculator.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font

@Composable
fun AppTheme(option: ThemeOption, content: @Composable () -> Unit) {
    val colors = when(option) {
        ThemeOption.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) darkColors() else lightColors()
        ThemeOption.DARK -> darkColors()
        ThemeOption.LIGHT -> lightColors()
    }

    MaterialTheme(
        typography = Typography(
            defaultFontFamily = FontFamily(Font("/RobotoCondensed-Regular.ttf"))
        ),
        colors = colors
    ) {
        content()
    }
}