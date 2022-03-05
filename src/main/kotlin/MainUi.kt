package garden.ephemeral.calculator

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainUi() {
    val state = remember { AppState() }
    val outputTextScrollState = rememberScrollState(0)
    val focusRequester = remember { FocusRequester() }
    val valueTextStyle = LocalTextStyle.current.copy(fontSize = 32.sp)

    Scaffold(
        bottomBar = {
            TextField(
                value = state.inputText,
                onValueChange = { newValue -> state.inputText = newValue },
                singleLine = true,
                textStyle = valueTextStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onKeyEvent { event ->
                        var consumed = false
                        if (event.key == Key.Enter) {
                            state.execute(valueTextStyle = valueTextStyle)
                            consumed = true
                        }
                        consumed
                    },
            )

            SideEffect {
                focusRequester.requestFocus()
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .padding(bottom = padding.calculateBottomPadding())
        ) {
            SelectionContainer {
                Text(
                    text = state.outputText,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.Bottom)
                        .verticalScroll(outputTextScrollState),
                )
            }
            VerticalScrollbar(
                rememberScrollbarAdapter(outputTextScrollState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
            )
        }
    }
}
