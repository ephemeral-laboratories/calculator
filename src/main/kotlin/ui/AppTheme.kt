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
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography(
            defaultFontFamily = FontFamily(Font("/RobotoCondensed-Regular.ttf"))
        ),
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
    ) {
        content()
    }
}