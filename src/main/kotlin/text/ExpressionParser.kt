package garden.ephemeral.calculator.text

import com.ibm.icu.text.NumberFormat
import garden.ephemeral.calculator.grammar.ExpressionLexer
import garden.ephemeral.calculator.grammar.ExpressionParser
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.Parentheses
import garden.ephemeral.calculator.nodes.functions.Function1Node
import garden.ephemeral.calculator.nodes.functions.Function2Node
import garden.ephemeral.calculator.nodes.ops.InfixOperator
import garden.ephemeral.calculator.nodes.ops.InfixOperatorNode
import garden.ephemeral.calculator.nodes.ops.PrefixOperator
import garden.ephemeral.calculator.nodes.ops.PrefixOperatorNode
import garden.ephemeral.calculator.nodes.values.Constant
import garden.ephemeral.calculator.nodes.values.ConstantNode
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.math.complex.Complex
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.antlr.v4.runtime.tree.ParseTree
import java.text.ParseException

class ExpressionParser(private val numberFormat: NumberFormat) {
    fun parse(input: String): Node {
        val errorListener = ErrorListener()

        val lexer = ExpressionLexer(CharStreams.fromString(input))
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)
        val parser = ExpressionParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)
        parser.errorHandler = BailErrorStrategy()

        val expression: ExpressionParser.ExpressionContext
        try {
            expression = parser.expression()
            if (errorListener.message != null) {
                throw ParseException("Parsed, but $errorListener", errorListener.start)
            }
        } catch (e: ParseCancellationException) {
            throw if (errorListener.message != null) {
                ParseException("Failed, because $errorListener", errorListener.start).also {
                    it.initCause(e)
                }
            } else {
                e
            }
        }

        return transform(expression)
    }

    private fun transform(tree: ParseTree): Node {
        return when (tree) {
            is ExpressionParser.ExpressionContext ->
                transform(tree.getChild(0))

            is ExpressionParser.ParenthesizedExpressionContext ->
                Parentheses(transform(tree.getChild(1)))

            is ExpressionParser.PlusExpressionContext ->
                InfixOperatorNode(InfixOperator.PLUS, transform(tree.getChild(0)), transform(tree.getChild(2)))

            is ExpressionParser.MinusExpressionContext ->
                InfixOperatorNode(InfixOperator.MINUS, transform(tree.getChild(0)), transform(tree.getChild(2)))

            is ExpressionParser.TimesExpressionContext ->
                InfixOperatorNode(InfixOperator.TIMES, transform(tree.getChild(0)), transform(tree.getChild(2)))

            is ExpressionParser.ImplicitTimesExpressionContext ->
                InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, transform(tree.getChild(0)), transform(tree.getChild(1)))

            is ExpressionParser.DivideExpressionContext ->
                InfixOperatorNode(InfixOperator.DIVIDE, transform(tree.getChild(0)), transform(tree.getChild(2)))

            is ExpressionParser.PowerExpressionContext ->
                InfixOperatorNode(InfixOperator.POWER, transform(tree.getChild(0)), transform(tree.getChild(2)))

            is ExpressionParser.UnaryMinusExpressionContext ->
                PrefixOperatorNode(PrefixOperator.UNARY_MINUS, transform(tree.getChild(1)))

            is ExpressionParser.FunctionExpressionContext ->
                transform(tree.getChild(0))

            is ExpressionParser.Function1ExpressionContext ->
                Function1Node.create(tree.getChild(0), transform(tree.getChild(2)))

            is ExpressionParser.Function2ExpressionContext ->
                Function2Node.create(tree.getChild(0), transform(tree.getChild(2)), transform(tree.getChild(4)))

            is ExpressionParser.ValueContext ->
                transform(tree.getChild(0))

            is ExpressionParser.RealNumberContext -> {
                val sign = signFromToken(tree.sign)
                val magnitude = numberFormat.parse(sign + tree.magnitude.text).toDouble()
                Value(magnitude)
            }

            is ExpressionParser.ComplexNumberContext -> {
                val real = if (tree.real != null) {
                    val realSign = signFromToken(tree.realSign)
                    numberFormat.parse(realSign + tree.real.text).toDouble()
                } else {
                    0.0
                }
                val imagSign = signFromToken(tree.imagSign)
                val imag = numberFormat.parse(imagSign + (tree.imag?.text ?: "1")).toDouble()
                Value(Complex(real, imag))
            }

            is ExpressionParser.ConstantContext ->
                if (tree.TAU() != null) {
                    ConstantNode(Constant.TAU)
                } else if (tree.PI() != null) {
                    ConstantNode(Constant.PI)
                } else if (tree.E() != null) {
                    ConstantNode(Constant.E)
                } else {
                    throw UnsupportedOperationException("Unknown tree node: $tree")
                }

            else -> throw UnsupportedOperationException("Unknown tree node: $tree")
        }
    }

    private fun signFromToken(token: Token?): String = if (token?.type == ExpressionLexer.MINUS) "-" else ""

    private class ErrorListener : BaseErrorListener() {
        var message: String? = null
        var start = -2
        var stop = -2
        var line = -2

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            if (message != null) {
                return
            }
            if (offendingSymbol is Token) {
                start = offendingSymbol.startIndex
                stop = offendingSymbol.stopIndex
            } else if (recognizer is ExpressionLexer) {
                start = recognizer._tokenStartCharIndex
                stop = recognizer._input.index()
            }
            this.line = line
            message = msg
        }

        override fun toString(): String {
            return "$start-$stop l.$line: $message"
        }
    }
}
