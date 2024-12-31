package garden.ephemeral.calculator.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberCursorPositionProvider
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    appState: AppState,
    drawerState: DrawerState,
    valueTextStyle: TextStyle,
    scope: CoroutineScope,
    padding: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Best effort to line the text up between the two views, but still off by 0.5 dp :(
            .padding(15.dp)
            .padding(padding),
    ) {
        ContextMenuArea(
            items = {
                listOf(
                    ContextMenuItem("Clear") {
                        appState.clearHistory()
                    },
                )
            },
        ) {
            SelectionContainer {
                LazyColumn(
                    modifier = Modifier
                        .testTag("HistoryList")
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.Bottom),
                    state = appState.outputState,
                ) {
                    items(appState.history) { item ->
                        Column {
                            // Newlines added here purely for people who copy the text
                            val input = item.input.prettyPrint(appState.valueFormat)
                            Text(text = "$input =\n", maxLines = 1)
                            val output = appState.valueFormat.format(item.output)
                            Text(text = "$output\n", maxLines = 1, style = valueTextStyle)
                        }
                    }
                }
            }
        }

        TooltipBox(
            positionProvider = rememberCursorPositionProvider(),
            tooltip = {
                Text(stringResource(Res.string.settings))
            },
            state = rememberTooltipState(),
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                modifier = Modifier
                    .testTag("SettingsButton")
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f), CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(Res.string.settings),
                )
            }
        }

        VerticalScrollbar(
            rememberScrollbarAdapter(appState.outputState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}
