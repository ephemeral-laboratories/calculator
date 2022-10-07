package garden.ephemeral.calculator

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import garden.ephemeral.calculator.ui.MainUi

// `singleWindowApplication` doesn't work if I want an icon:
// https://github.com/JetBrains/compose-jb/issues/2369
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Calculator",
        icon = painterResource("/AppIcon.ico"),
    ) {
        MainUi()
    }
}
