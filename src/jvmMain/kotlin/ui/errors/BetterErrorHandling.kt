package garden.ephemeral.calculator.ui.errors

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.LocalWindowExceptionHandlerFactory
import androidx.compose.ui.window.WindowExceptionHandler
import androidx.compose.ui.window.WindowExceptionHandlerFactory
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.error_dialog_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import java.awt.Dialog
import java.awt.Point
import java.awt.Window
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JDialog
import javax.swing.KeyStroke

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
@Composable
fun BetterErrorHandling(content: @Composable () -> Unit) {
    val settings = ErrorDialogSettings(
        title = stringResource(Res.string.error_dialog_title),
        colorScheme = MaterialTheme.colorScheme,
        shapes = MaterialTheme.shapes,
        typography = MaterialTheme.typography,
    )
    val state = remember { ErrorDialogState(settings) }

    CompositionLocalProvider(LocalWindowExceptionHandlerFactory provides CustomWindowExceptionHandlerFactory(state)) {
        content()
    }

    val errorInfo = state.errorInfo
    if (errorInfo != null) {
        DialogWindow(
            create = {
                ComposeDialog(
                    owner = errorInfo.window,
                    modalityType = Dialog.ModalityType.DOCUMENT_MODAL,
                ).also { dialog ->
                    dialog.title = state.settings.title
                    bindCloseAction(dialog)
                }
            },
            update = { dialog ->
                dialog.pack()
                dialog.isResizable = false
                setDialogLocationRelativeTo(dialog, errorInfo.window)
            },
            dispose = ComposeDialog::dispose,
        ) {
            MaterialTheme(
                colorScheme = state.settings.colorScheme,
                shapes = state.settings.shapes,
                typography = state.settings.typography,
            ) {
                BetterErrorPane(throwable = errorInfo.throwable, onDismissClicked = state::dismissError)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private class CustomWindowExceptionHandlerFactory(private val state: ErrorDialogState) :
    WindowExceptionHandlerFactory {
    override fun exceptionHandler(window: Window): WindowExceptionHandler {
        return CustomWindowExceptionHandler(window, state)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private class CustomWindowExceptionHandler(private val window: Window, private val state: ErrorDialogState) :
    WindowExceptionHandler {
    override fun onException(throwable: Throwable) {
        state.showError(throwable = throwable, window = window)
    }
}

private fun bindCloseAction(dialog: JDialog) {
    // Wiring up ESC to work correctly since apparently that's not default behaviour. >:-/
    dialog.rootPane.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close")
    dialog.rootPane.actionMap.put(
        "close",
        object : AbstractAction("close") {
            override fun actionPerformed(e: ActionEvent?) {
                dialog.dispose()
            }
        },
    )
}

private fun setDialogLocationRelativeTo(dialog: JDialog, parent: Window) {
    // `dialog.setLocationRelativeTo` doesn't work, seemingly because the parent we pass in is a `Window`?
    // Their definition of "is showing" might always return false for windows, even if the window is showing.
    dialog.location = Point(
        /* x = */ parent.x + (parent.width - dialog.width) / 2,
        /* y = */ parent.y + (parent.height - dialog.height) / 2,
    )
}
