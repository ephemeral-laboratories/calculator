package garden.ephemeral.calculator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import garden.ephemeral.calculator.calculator.generated.resources.AppIcon
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.ui.MainUi
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

// `singleWindowApplication` doesn't work if I want an icon:
// https://github.com/JetBrains/compose-jb/issues/2369
@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Calculator",
        icon = painterResource(Res.drawable.AppIcon),
    ) {
        MainUi()
    }
}
