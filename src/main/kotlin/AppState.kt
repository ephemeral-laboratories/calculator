package garden.ephemeral.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import garden.ephemeral.calculator.text.NumberFormats
import java.text.ParseException

class AppState {
    var outputText by mutableStateOf(AnnotatedString(""))
    var inputText by mutableStateOf(TextFieldValue(""))
    val numberFormat = NumberFormats.dozenalFormat

    fun execute(valueTextStyle: TextStyle) {
        val text = inputText.text
        if (text.isNotEmpty()) {
            val expression = try {
                ExpressionParser(numberFormat).parse(text)
            } catch (e: ParseException) {
                // TODO: Beep and/or shake or highlight errors in the input field or something
                return
            }

            val expressionString = expression.prettyPrint(numberFormat)
            val result = expression.evaluate().prettyPrint(numberFormat)
            outputText += AnnotatedString("$expressionString =\n")
            outputText += AnnotatedString("$result\n", valueTextStyle.toSpanStyle())
        }
        inputText = TextFieldValue()
    }
}
