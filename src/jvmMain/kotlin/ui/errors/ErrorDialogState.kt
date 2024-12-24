package garden.ephemeral.calculator.ui.errors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.awt.Window

internal class ErrorDialogState {
    data class ErrorInfo(
        val throwable: Throwable,
        val window: Window,
    )

    var errorInfo by mutableStateOf(null as ErrorInfo?)

    fun showError(throwable: Throwable, window: Window) {
        errorInfo = ErrorInfo(throwable = throwable, window = window)
    }

    fun dismissError() {
        errorInfo = null
    }
}

@Composable
internal fun rememberErrorDialogState() = remember { ErrorDialogState() }
