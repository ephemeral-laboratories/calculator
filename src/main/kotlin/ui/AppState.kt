package garden.ephemeral.calculator.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Colors
import androidx.compose.runtime.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import garden.ephemeral.calculator.text.ExpressionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.ParseException

class AppState {
    val outputState = LazyListState()
    var inputText by mutableStateOf(TextFieldValue(""))
    var isInputError by mutableStateOf(false)
    var themeOption by mutableStateOf(ThemeOption.SYSTEM_DEFAULT)
    var numberFormatOption by mutableStateOf(NumberFormatOption.DECIMAL)
    var decimalRadixSeparatorOption by mutableStateOf(NumberFormatOption.DECIMAL.defaultRadixSeparator())
    var dozenalRadixSeparatorOption by mutableStateOf(NumberFormatOption.DOZENAL.defaultRadixSeparator())
    val history: MutableList<HistoryEntry> = mutableStateListOf()

    val numberFormat by derivedStateOf {
        val radixSeparatorOption = when (numberFormatOption) {
            NumberFormatOption.DOZENAL -> dozenalRadixSeparatorOption
            NumberFormatOption.DECIMAL -> decimalRadixSeparatorOption
        }
        numberFormatOption.numberFormatFactory(radixSeparatorOption.symbol)
    }

    fun updateInputText(newValue: TextFieldValue) {
        isInputError = false
        inputText = newValue
        if (newValue.text.contains('\n')) {
            inputText = newValue.copy(text = newValue.text.replace("\n", ""))
        }
    }

    fun execute(colorScheme: Colors, scope: CoroutineScope) {
        val text = inputText.text
        if (text.isNotEmpty()) {
            val expression = try {
                ExpressionParser(numberFormat).parse(text)
            } catch (e: ParseException) {
                // XXX: Playing a sound would be nice too
                isInputError = true
                inputText = inputText.copy(
                    annotatedString = buildAnnotatedString {
                        append(inputText.text)
                        addStyle(
                            SpanStyle(color = colorScheme.error, textDecoration = TextDecoration.Underline),
                            e.errorOffset, e.errorOffset + 1
                        )
                    }
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
}
