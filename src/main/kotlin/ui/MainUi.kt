package garden.ephemeral.calculator.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.input_error
import garden.ephemeral.calculator.calculator.generated.resources.number_format
import garden.ephemeral.calculator.calculator.generated.resources.radix_separator
import garden.ephemeral.calculator.calculator.generated.resources.settings
import garden.ephemeral.calculator.calculator.generated.resources.theme
import garden.ephemeral.calculator.ui.common.Localizable
import garden.ephemeral.calculator.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KMutableProperty0

@Composable
fun MainUi() {
    val appState = remember { AppState() }

    AppTheme(appState.themeOption) {
        val valueTextStyle = LocalTextStyle.current.copy(fontSize = 32.sp)
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerContent = @Composable { DrawerContent(appState) },
            drawerState = drawerState,
        ) {
            Scaffold(
                bottomBar = @Composable { BottomBarContent(appState, valueTextStyle, scope) },
            ) { padding ->
                MainContent(appState, drawerState, valueTextStyle, scope, padding)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawerContent(appState: AppState) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp),
        ) {
            OptionDropDown(
                label = stringResource(Res.string.theme),
                values = ThemeOption.entries,
                property = appState::themeOption,
            )
            OptionDropDown(
                label = stringResource(Res.string.number_format),
                values = NumberFormatOption.entries,
                property = appState::numberFormatOption,
            )
            OptionDropDown(
                label = stringResource(Res.string.radix_separator),
                values = RadixSeparatorOption.entries,
                property = when (appState.numberFormatOption) {
                    NumberFormatOption.DECIMAL -> appState::decimalRadixSeparatorOption
                    NumberFormatOption.DOZENAL -> appState::dozenalRadixSeparatorOption
                },
            )

            Box(
                modifier = Modifier.fillMaxHeight().padding(16.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                val version = System.getProperty("jpackage.app-version.unmangled") ?: "[DEV]"
                Text("Version $version")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Localizable> OptionDropDown(
    label: String,
    values: List<T>,
    property: KMutableProperty0<T>,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { b -> expanded = b },
    ) {
        Column {
            TextField(
                value = property.get().localizedName,
                label = {
                    Text(text = label)
                },
                onValueChange = {},
                modifier = Modifier.menuAnchor(),
                readOnly = true,
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                values.forEach { value ->
                    val scope = rememberCoroutineScope()
                    DropdownMenuItem(
                        text = {
                            Text(value.localizedName)
                        },
                        onClick = {
                            property.set(value)
                            scope.launch {
                                delay(150)
                                expanded = false
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomBarContent(appState: AppState, valueTextStyle: TextStyle, scope: CoroutineScope) {
    val focusRequester = remember { FocusRequester() }
    val colorScheme = MaterialTheme.colorScheme

    TextField(
        value = appState.inputText,
        onValueChange = appState::updateInputText,
        trailingIcon = @Composable {
            if (appState.isInputError) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = stringResource(Res.string.input_error),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        },
        isError = appState.isInputError,
        singleLine = true,
        textStyle = valueTextStyle,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                var consumed = false
                if (event.key == Key.Enter) {
                    appState.execute(colorScheme = colorScheme, scope = scope)
                    consumed = true
                }
                consumed
            },
    )

    SideEffect {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalResourceApi::class)
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
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.Bottom),
                    state = appState.outputState,
                ) {
                    items(appState.history) { item ->
                        Column {
                            // Newlines added here purely for people who copy the text
                            val input = item.input.prettyPrint(appState.valueFormat)
                            Text(text = "$input =\n", maxLines = 1)
                            val output = item.output.prettyPrint(appState.valueFormat)
                            Text(text = "$output\n", maxLines = 1, style = valueTextStyle)
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = {
                scope.launch {
                    drawerState.open()
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f), CircleShape),
        ) {
            Icon(Icons.Outlined.Settings, stringResource(Res.string.settings))
        }

        VerticalScrollbar(
            rememberScrollbarAdapter(appState.outputState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}
