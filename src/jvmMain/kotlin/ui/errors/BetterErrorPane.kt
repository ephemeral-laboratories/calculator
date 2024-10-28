package garden.ephemeral.calculator.ui.errors

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.error_dialog_content_action
import garden.ephemeral.calculator.calculator.generated.resources.error_dialog_dismiss
import garden.ephemeral.calculator.calculator.generated.resources.error_dialog_icon
import garden.ephemeral.calculator.ui.ThemeOption
import garden.ephemeral.calculator.ui.theme.AppTheme
import garden.ephemeral.calculator.ui.util.rememberIconPainter
import org.jetbrains.compose.resources.stringResource
import javax.swing.UIManager

@Composable
@Preview
internal fun BetterErrorPanePreview() {
    AppTheme(ThemeOption.DARK) {
        BetterErrorPane(
            throwable = RuntimeException("Synthetic exception for preview!"),
            onDismissClicked = {},
        )
    }
}

@Composable
internal fun BetterErrorPane(throwable: Throwable, onDismissClicked: () -> Unit) {
    Surface(
        modifier = Modifier
            .testTag("BetterErrorPane")
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .widthIn(max = 720.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                SwingErrorIcon()

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = stringResource(Res.string.error_dialog_content_action), style = MaterialTheme.typography.bodyLarge)

                    SelectionContainer {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = throwable.message ?: "", style = MaterialTheme.typography.bodyLarge)
                            Box(
                                modifier = Modifier
                                    .height(240.dp)
                                    .fillMaxWidth()
                                    .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical),
                            ) {
                                Text(text = throwable.stackTraceToString(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismissClicked) {
                    Text(text = stringResource(Res.string.error_dialog_dismiss))
                }
            }
        }
    }
}

@Composable
private fun SwingErrorIcon() {
    // Some Swing esoterica here.
    Icon(
        painter = rememberIconPainter { UIManager.getIcon("OptionPane.errorIcon") },
        contentDescription = stringResource(Res.string.error_dialog_icon),
        tint = Color.Unspecified,
    )
}
