package garden.ephemeral.calculator.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import garden.ephemeral.calculator.text.ExpressionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.ParseException

class AppState {
    val outputState = LazyListState()
    var inputText by mutableStateOf(TextFieldValue(""))
    var numberFormatOption by mutableStateOf(NumberFormatOption.DECIMAL)
    var decimalRadixSeparatorOption by mutableStateOf(RadixSeparatorOption.PERIOD)
    var dozenalRadixSeparatorOption by mutableStateOf(RadixSeparatorOption.SEMICOLON)
    val history: MutableList<HistoryEntry> = mutableStateListOf()

    val numberFormat by derivedStateOf {
        val radixSeparatorOption = when(numberFormatOption) {
            NumberFormatOption.DOZENAL -> dozenalRadixSeparatorOption
            NumberFormatOption.DECIMAL -> decimalRadixSeparatorOption
        }
        numberFormatOption.numberFormatFactory(radixSeparatorOption.symbol)
    }

    fun execute(scope: CoroutineScope) {
        val text = inputText.text
        if (text.isNotEmpty()) {
            val expression = try {
                ExpressionParser(numberFormat).parse(text)
            } catch (e: ParseException) {
                // TODO: Beep and/or shake or highlight errors in the input field or something
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
