package garden.ephemeral.calculator

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography(
            defaultFontFamily = FontFamily(Font("/RobotoCondensed-Regular.ttf"))
        ),
    ) {
        content()
    }
}