package garden.ephemeral.calculator.ui.errors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.LocalWindowExceptionHandlerFactory
import androidx.compose.ui.window.WindowExceptionHandler
import androidx.compose.ui.window.WindowExceptionHandlerFactory
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.error_dialog_title
import org.jetbrains.compose.resources.stringResource
import java.awt.Dialog
import java.awt.Window
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JDialog
import javax.swing.KeyStroke

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BetterErrorHandling(content: @Composable () -> Unit) {
    val state = rememberErrorDialogState()

    CompositionLocalProvider(LocalWindowExceptionHandlerFactory provides CustomWindowExceptionHandlerFactory(state)) {
        content()
    }

    // Error dialog itself is outside the CompositionLocalProvider override above,
    // because we don't want an error in our own dialog to overwrite the real one.
    ErrorDialogWindow(state)
}

@Composable
private fun ErrorDialogWindow(state: ErrorDialogState) {
    val errorInfo = state.errorInfo
    if (errorInfo != null) {
        val dialogTitle = stringResource(Res.string.error_dialog_title)

        // Avoid using the window if it is not displayable, e.g. when an error occurs initialising it.
        // If you use it in that state, you just get another error.
        val owningWindow = errorInfo.window.takeIf { it.isDisplayable }

        DialogWindow(
            create = {
                ComposeDialog(
                    owner = owningWindow,
                    modalityType = Dialog.ModalityType.DOCUMENT_MODAL,
                ).also { dialog ->
                    dialog.title = dialogTitle
                    bindCloseAction(dialog)
                }
            },
            update = { dialog ->
                dialog.pack()
                dialog.isResizable = false
                dialog.setLocationRelativeTo(owningWindow)
            },
            dispose = ComposeDialog::dispose,
        ) {
            BetterErrorPane(throwable = errorInfo.throwable, onDismissClicked = state::dismissError)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private class CustomWindowExceptionHandlerFactory(private val state: ErrorDialogState) :
    WindowExceptionHandlerFactory {

    override fun exceptionHandler(window: Window) = WindowExceptionHandler { throwable ->
        state.showError(throwable = throwable, window = window)
    }
}

private const val CLOSE_ACTION_KEY = "close"

private fun bindCloseAction(dialog: JDialog) {
    // Wiring up ESC to work correctly since apparently that's not default behaviour. >:-/
    dialog.rootPane.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE_ACTION_KEY)
    dialog.rootPane.actionMap.put(
        CLOSE_ACTION_KEY,
        object : AbstractAction(CLOSE_ACTION_KEY) {
            override fun actionPerformed(e: ActionEvent?) {
                dialog.dispose()
            }
        },
    )
}
