package garden.ephemeral.calculator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        title = "Calculator",
        onCloseRequest = ::exitApplication
    ) {
        AppTheme {
            MainUi()
        }
    }
}
