package garden.ephemeral.calculator.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.input_error
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBar(appState: AppState, valueTextStyle: TextStyle, scope: CoroutineScope) {
    val focusRequester = remember { FocusRequester() }
    val colorScheme = MaterialTheme.colorScheme

    TextField(
        value = appState.inputText,
        onValueChange = appState::updateInputText,
        trailingIcon = @Composable {
            if (appState.isInputError) {
                Icon(
                    imageVector = Icons.Outlined.Error,
                    contentDescription = stringResource(Res.string.input_error),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("MainTextFieldErrorIcon"),
                )
            }
        },
        isError = appState.isInputError,
        singleLine = true,
        textStyle = valueTextStyle,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            appState.execute(colorScheme = colorScheme, scope = scope)
        }),
        modifier = Modifier
            .testTag("MainTextField")
            .fillMaxWidth()
            .focusRequester(focusRequester),
    )

    SideEffect {
        focusRequester.requestFocus()
    }
}
