package garden.ephemeral.calculator.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import garden.ephemeral.calculator.ui.common.Localizable
import garden.ephemeral.calculator.ui.components.ExposedDropDownMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty0

@Composable
fun MainUi() {
    val appState = remember { AppState() }

    AppTheme(appState.themeOption) {
        val valueTextStyle = LocalTextStyle.current.copy(fontSize = 32.sp)
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        val customDrawerShape: Shape = object : Shape {
            override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
                return Outline.Rectangle(Rect(left = 0.0f, top = 0.0f, right = 200.0f, bottom = size.height))
            }
        }

        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = @Composable { DrawerContent(appState) },
            drawerShape = customDrawerShape,
            bottomBar = @Composable { BottomBarContent(appState, valueTextStyle, scope) }
        ) { padding ->
            MainContent(appState, scaffoldState, valueTextStyle, scope, padding)
        }
    }
}

@Composable
fun DrawerContent(appState: AppState) {
    OptionDropDown(
        label = AppStrings.Theme,
        values = ThemeOption.values(),
        property = appState::themeOption
    )
    OptionDropDown(
        label = AppStrings.NumberFormat,
        values = NumberFormatOption.values(),
        property = appState::numberFormatOption
    )
    OptionDropDown(
        label = AppStrings.RadixSeparator,
        values = RadixSeparatorOption.values(),
        property = when (appState.numberFormatOption) {
            NumberFormatOption.DECIMAL -> appState::decimalRadixSeparatorOption
            NumberFormatOption.DOZENAL -> appState::dozenalRadixSeparatorOption
        }
    )
}

@Composable
private fun <T : Localizable> OptionDropDown(
    label: String,
    values: Array<T>,
    property: KMutableProperty0<T>,
) {
    ExposedDropDownMenu(
        values = values.asIterable(),
        selectedValue = property.get(),
        onChange = property::set,
        label = @Composable { Text(text = label, softWrap = false) },
        modifier = Modifier
            // Works around the longer label not pushing the size to be bigger
            .width(180.dp)
            .padding(start = 16.dp, top = 16.dp),
    )
}

@Composable
fun BottomBarContent(appState: AppState, valueTextStyle: TextStyle, scope: CoroutineScope) {
    val focusRequester = remember { FocusRequester() }
    val colorScheme = MaterialTheme.colors

    TextField(
        value = appState.inputText,
        onValueChange = appState::updateInputText,
        trailingIcon = @Composable {
            if (appState.isInputError) {
                Icon(Icons.Filled.Error, AppStrings.InputError, tint = MaterialTheme.colors.error)
            }
        },
        isError = appState.isInputError,
        singleLine = true,
        textStyle = valueTextStyle,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onKeyEvent @OptIn(ExperimentalComposeUiApi::class) { event ->
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

@Composable
fun MainContent(
    appState: AppState,
    scaffoldState: ScaffoldState,
    valueTextStyle: TextStyle,
    scope: CoroutineScope,
    padding: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Best effort to line the text up between the two views, but still off by 0.5 dp :(
            .padding(15.dp)
            .padding(padding)
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
                        val input = item.input.prettyPrint(appState.numberFormat)
                        Text(text = "$input =\n", maxLines = 1)
                        val output = item.output.prettyPrint(appState.numberFormat)
                        Text(text = "$output\n", maxLines = 1, style = valueTextStyle)
                    }
                }
            }
        }
        IconButton(
            onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colors.background.copy(alpha = 0.95f), CircleShape),
        ) {
            Icon(Icons.Outlined.Settings, AppStrings.Settings)
        }
        VerticalScrollbar(
            rememberScrollbarAdapter(appState.outputState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}
