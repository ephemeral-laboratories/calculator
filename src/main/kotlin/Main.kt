package garden.ephemeral.calculator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import garden.ephemeral.calculator.ui.AppTheme
import garden.ephemeral.calculator.ui.MainUi

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
