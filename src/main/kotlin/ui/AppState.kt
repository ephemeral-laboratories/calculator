package garden.ephemeral.calculator.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import garden.ephemeral.calculator.text.ExpressionParser
import garden.ephemeral.calculator.text.ValueFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.text.ParseException

@OptIn(ExperimentalResourceApi::class)
class AppState {
    val outputState = LazyListState()
    var inputText by mutableStateOf(TextFieldValue(""))
    var isInputError by mutableStateOf(false)
    var themeOption by mutableStateOf(ThemeOption.SYSTEM_DEFAULT)
    var numberFormatOption by mutableStateOf(NumberFormatOption.DECIMAL)
    var decimalRadixSeparatorOption by mutableStateOf(RadixSeparatorOption.defaultFor(NumberFormatOption.DECIMAL))
    var dozenalRadixSeparatorOption by mutableStateOf(RadixSeparatorOption.defaultFor(NumberFormatOption.DOZENAL))
    val history: MutableList<HistoryEntry> = mutableStateListOf()

    val valueFormat by derivedStateOf {
        val radixSeparatorOption = when (numberFormatOption) {
            NumberFormatOption.DOZENAL -> dozenalRadixSeparatorOption
            NumberFormatOption.DECIMAL -> decimalRadixSeparatorOption
        }
        val symbols = numberFormatOption.defaultSymbols.copy(
            radixSeparator = radixSeparatorOption.symbol,
            // XXX: Is there any way to sensibly load a resource string from here? :(
            // notANumber = stringResource(Res.string.undefined),
        )
        ValueFormat(numberFormatOption.radix, symbols)
    }

    fun updateInputText(newValue: TextFieldValue) {
        isInputError = false
        inputText = newValue
        if (newValue.text.contains('\n')) {
            inputText = newValue.copy(text = newValue.text.replace("\n", ""))
        }
    }

    fun execute(colorScheme: ColorScheme, scope: CoroutineScope) {
        val text = inputText.text
        if (text.isNotEmpty()) {
            val expression = try {
                ExpressionParser(valueFormat.realFormat).parse(text)
            } catch (e: ParseException) {
                // XXX: Playing a sound would be nice too
                isInputError = true
                inputText = inputText.copy(
                    annotatedString = buildAnnotatedString {
                        append(inputText.text)
                        // Sometimes the error is at EOS, so we add something to the text just so that
                        // we will have something to highlight and select.
                        if (e.errorOffset >= inputText.text.length) {
                            append('?')
                        }
                        addStyle(
                            style = SpanStyle(color = colorScheme.error, textDecoration = TextDecoration.Underline),
                            start = e.errorOffset,
                            end = e.errorOffset + 1,
                        )
                    },
                    selection = TextRange(e.errorOffset, e.errorOffset + 1),
                )
                return
            }

            history.add(HistoryEntry(expression, expression.evaluate()))

            scope.launch {
                outputState.animateScrollToItem(history.lastIndex)
            }
        }
        inputText = TextFieldValue()
    }

    fun clearHistory() {
        history.clear()
    }
}
