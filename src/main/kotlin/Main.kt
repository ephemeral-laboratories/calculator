package garden.ephemeral.calculator

import androidx.compose.ui.window.singleWindowApplication
import garden.ephemeral.calculator.ui.MainUi

fun main() = singleWindowApplication(
    title = "Calculator",
) {
    MainUi()
}
