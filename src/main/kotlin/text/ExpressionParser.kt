package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.grammar.ExpressionLexer
import garden.ephemeral.calculator.grammar.ExpressionParser
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.Parentheses
import garden.ephemeral.calculator.nodes.functions.Function1
import garden.ephemeral.calculator.nodes.functions.Function1Node
import garden.ephemeral.calculator.nodes.functions.Function2
import garden.ephemeral.calculator.nodes.functions.Function2Node
import garden.ephemeral.calculator.nodes.operators.InfixOperator
import garden.ephemeral.calculator.nodes.operators.InfixOperatorNode
import garden.ephemeral.calculator.nodes.operators.PrefixOperator
import garden.ephemeral.calculator.nodes.operators.PrefixOperatorNode
import garden.ephemeral.calculator.nodes.values.Constant
import garden.ephemeral.calculator.nodes.values.ConstantNode
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.math.complex.Complex
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.antlr.v4.runtime.tree.ParseTree
import java.text.ParseException

class ExpressionParser(private val realFormat: PositionalFormat) {
    fun parse(input: String): Node {
        val errorListener = ErrorListener()

        val lexer = ExpressionLexer(CharStreams.fromString(input))
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)
        val parser = ExpressionParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)
        parser.errorHandler = BailErrorStrategy()

        val expression: ExpressionParser.StartContext
        try {
            expression = parser.start()
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
            is ExpressionParser.StartContext ->
                transform(tree.expression())

            is ExpressionParser.ParenthesizedExpressionContext ->
                Parentheses(transform(tree.expression()))

            is ExpressionParser.PlusExpressionContext -> {
                val children = tree.plusChildExpression()
                var node = InfixOperatorNode(
                    InfixOperator.PLUS,
                    transform(children[0]),
                    transform(children[1]),
                )
                children.asSequence().drop(2).forEach { expression ->
                    node = InfixOperatorNode(InfixOperator.PLUS, node, transform(expression))
                }
                node
            }

            is ExpressionParser.MinusExpressionContext -> InfixOperatorNode(
                InfixOperator.MINUS,
                transform(tree.minusChildExpression(0)),
                transform(tree.minusChildExpression(1)),
            )

            is ExpressionParser.TimesExpressionContext -> {
                val children = tree.timesChildExpression()
                var node = InfixOperatorNode(
                    InfixOperator.TIMES,
                    transform(children[0]),
                    transform(children[1]),
                )
                children.asSequence().drop(2).forEach { expression ->
                    node = InfixOperatorNode(InfixOperator.TIMES, node, transform(expression))
                }
                node
            }

            is ExpressionParser.ImplicitTimesExpressionContext -> {
                val first = tree.implicitTimesFirstChildExpression()
                val children = tree.implicitTimesChildExpression()
                var node = InfixOperatorNode(
                    InfixOperator.IMPLICIT_TIMES,
                    transform(first),
                    transform(children[0]),
                )
                children.asSequence().drop(1).forEach { expression ->
                    node = InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, node, transform(expression))
                }
                node
            }

            is ExpressionParser.DivideExpressionContext -> InfixOperatorNode(
                InfixOperator.DIVIDE,
                transform(tree.divideChildExpression(0)),
                transform(tree.divideChildExpression(1)),
            )

            is ExpressionParser.PowerExpressionContext -> InfixOperatorNode(
                InfixOperator.POWER,
                transform(tree.powerChildExpression(0)),
                transform(tree.powerChildExpression(1)),
            )

            is ExpressionParser.UnaryMinusExpressionContext -> {
                // Simplify things like -2 to a single value
                val childNode = transform(tree.unaryMinusChildExpression())
                if (childNode is Value) {
                    Value(PrefixOperator.UNARY_MINUS.apply(childNode.value))
                } else {
                    PrefixOperatorNode(PrefixOperator.UNARY_MINUS, childNode)
                }
            }

            is ExpressionParser.Function1ExpressionContext -> {
                val name = tree.func.text
                val function = Function1.findByName(name)
                    ?: throw ParseException("Function not found: $name", tree.func.startIndex)
                Function1Node(function, transform(tree.arg))
            }

            is ExpressionParser.Function2ExpressionContext -> {
                val name = tree.func.text
                val function = Function2.findByName(name)
                    ?: throw ParseException("Function not found: $name", tree.func.startIndex)
                Function2Node(function, transform(tree.arg1), transform(tree.arg2))
            }

            is ExpressionParser.RealNumberContext -> {
                val real = parseReal(tree.magnitude)
                Value(real)
            }

            is ExpressionParser.ComplexNumberContext -> {
                val real = signFromToken(tree.realSign) * if (tree.real != null) {
                    parseReal(tree.real)
                } else {
                    0.0
                }
                val imag = signFromToken(tree.imagSign) * if (tree.imag != null) {
                    parseReal(tree.imag)
                } else {
                    // Even if there's no imag token, there must have still been an i token,
                    // so we want 1 for the imaginary part, not 0.
                    1.0
                }
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

            // All the nodes which are just wrappers for alternatives.
            // For these, we _could_ write long-winded expressions like:
            //     tree.branch1() ?: tree.branch2() ?: tree.branch3() ?: ...
            // But it's ugly in its own way, so we'll just treat them all
            // the same and unwrap the single child.
            is ExpressionParser.ExpressionContext,
            is ExpressionParser.PlusChildExpressionContext,
            is ExpressionParser.MinusChildExpressionContext,
            is ExpressionParser.TimesChildExpressionContext,
            is ExpressionParser.ImplicitTimesFirstChildExpressionContext,
            is ExpressionParser.ImplicitTimesChildExpressionContext,
            is ExpressionParser.DivideChildExpressionContext,
            is ExpressionParser.PowerChildExpressionContext,
            is ExpressionParser.UnaryMinusChildExpressionContext,
            is ExpressionParser.FunctionExpressionContext,
            is ExpressionParser.ValueContext,
            ->
                transform(tree.getChild(0))

            else -> throw UnsupportedOperationException("Unknown tree node: $tree")
        }
    }

    private fun parseReal(token: Token): Double {
        try {
            return realFormat.parse(token.text) as Double
        } catch (e: ParseException) {
            // Rethrowing with the right index for the full input string
            throw ParseException("Failure parsing number", token.startIndex).also {
                it.initCause(e)
            }
        }
    }

    private fun signFromToken(token: Token?): Double = if (token?.type == ExpressionLexer.MINUS) -1.0 else 1.0

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
            e: RecognitionException?,
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
