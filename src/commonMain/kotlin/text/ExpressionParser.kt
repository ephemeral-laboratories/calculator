package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.functions.Function1
import garden.ephemeral.calculator.functions.Function2
import garden.ephemeral.calculator.grammar.ExpressionLexer
import garden.ephemeral.calculator.grammar.ExpressionParser
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.Parentheses
import garden.ephemeral.calculator.nodes.functions.Function1Node
import garden.ephemeral.calculator.nodes.functions.Function2Node
import garden.ephemeral.calculator.nodes.operators.InfixOperatorNode
import garden.ephemeral.calculator.nodes.operators.PostfixOperatorNode
import garden.ephemeral.calculator.nodes.operators.PrefixOperatorNode
import garden.ephemeral.calculator.nodes.values.ConstantNode
import garden.ephemeral.calculator.nodes.values.ValueNode
import garden.ephemeral.calculator.operators.InfixOperator
import garden.ephemeral.calculator.operators.PostfixOperator
import garden.ephemeral.calculator.operators.PrefixOperator
import garden.ephemeral.calculator.values.Constant
import garden.ephemeral.calculator.values.Value
import org.antlr.v4.kotlinruntime.BailErrorStrategy
import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.misc.ParseCancellationException
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNodeImpl

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
                throw ParseException(message = "Parsed, but $errorListener", errorOffset = errorListener.start)
            }
        } catch (e: ParseCancellationException) {
            throw if (errorListener.message != null) {
                ParseException(
                    message = "Failed, because $errorListener",
                    errorOffset = errorListener.start,
                    cause = e,
                )
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

            is ExpressionParser.PlusMinusExpressionContext -> {
                var node = transform(tree.getChild(0)!!)
                for (i in 1 until tree.childCount step 2) {
                    val operator = (tree.getChild(i) as TerminalNodeImpl).symbol
                    val nextChild = tree.getChild(i + 1)!!
                    node = InfixOperatorNode(
                        if (operator.type == ExpressionLexer.Tokens.PLUS) InfixOperator.PLUS else InfixOperator.MINUS,
                        node,
                        transform(nextChild),
                    )
                }
                node
            }

            is ExpressionParser.TimesDivideExpressionContext -> {
                var node = transform(tree.getChild(0)!!)
                for (i in 1 until tree.childCount step 2) {
                    val operator = (tree.getChild(i) as TerminalNodeImpl).symbol
                    val nextChild = tree.getChild(i + 1)!!
                    node = InfixOperatorNode(
                        if (operator.type == ExpressionLexer.Tokens.TIMES) InfixOperator.TIMES else InfixOperator.DIVIDE,
                        node,
                        transform(nextChild),
                    )
                }
                node
            }

            is ExpressionParser.ImplicitTimesExpressionContext -> {
                var node = transform(tree.implicitTimesFirstChildExpression())
                for (i in 1 until tree.childCount) {
                    val nextChild = tree.getChild(i)!!
                    node = InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, node, transform(nextChild))
                }
                node
            }

            is ExpressionParser.PowerExpressionContext -> InfixOperatorNode(
                InfixOperator.POWER,
                transform(tree.powerChildExpression(0)!!),
                transform(tree.powerChildExpression(1)!!),
            )

            is ExpressionParser.UnaryMinusExpressionContext -> {
                // Simplify things like -2 to a single value
                val childNode = transform(tree.unaryMinusChildExpression())
                if (childNode is ValueNode) {
                    ValueNode(PrefixOperator.UNARY_MINUS.apply(childNode.value))
                } else {
                    PrefixOperatorNode(PrefixOperator.UNARY_MINUS, childNode)
                }
            }

            is ExpressionParser.DegreeExpressionContext -> {
                val childNode = transform(tree.degreeChildExpression())
                PostfixOperatorNode(PostfixOperator.DEGREES, childNode)
            }

            is ExpressionParser.Function1ExpressionContext -> {
                val func = tree.func!!
                val name = func.text!!
                val function = Function1.findByName(name)
                    ?: throw ParseException(message = "Function not found: $name", errorOffset = func.startIndex)
                Function1Node(function, transform(tree.arg!!))
            }

            is ExpressionParser.Function2ExpressionContext -> {
                val func = tree.func!!
                val name = func.text!!
                val function = Function2.findByName(name)
                    ?: throw ParseException(message = "Function not found: $name", errorOffset = func.startIndex)
                Function2Node(function, transform(tree.arg1!!), transform(tree.arg2!!))
            }

            is ExpressionParser.RealNumberContext -> {
                val real = parseReal(tree.magnitude!!)
                ValueNode(Value.OfReal(real))
            }

            is ExpressionParser.ComplexNumberContext -> {
                val real = signFromToken(tree.realSign) * if (tree.real != null) {
                    parseReal(tree.real!!)
                } else {
                    Real.ZERO
                }
                val imag = signFromToken(tree.imagSign) * if (tree.imag != null) {
                    parseReal(tree.imag!!)
                } else {
                    // Even if there's no imag token, there must have still been an i token,
                    // so we want 1 for the imaginary part, not 0.
                    Real.ONE
                }
                ValueNode(Value.OfComplex(Complex(real, imag)))
            }

            is ExpressionParser.ConstantContext ->
                if (tree.TAU() != null) {
                    ConstantNode(Constant.TAU)
                } else if (tree.PI() != null) {
                    ConstantNode(Constant.PI)
                } else if (tree.E() != null) {
                    ConstantNode(Constant.E)
                } else if (tree.PHI() != null) {
                    ConstantNode(Constant.PHI)
                } else {
                    throw UnsupportedOperationException("Unknown tree node: $tree")
                }

            // All the nodes which are just wrappers for alternatives.
            // For these, we _could_ write long-winded expressions like:
            //     tree.branch1() ?: tree.branch2() ?: tree.branch3() ?: ...
            // But it's ugly in its own way, so we'll just treat them all
            // the same and unwrap the single child.
            is ExpressionParser.ExpressionContext,
            is ExpressionParser.PlusMinusChildExpressionContext,
            is ExpressionParser.TimesDivideChildExpressionContext,
            is ExpressionParser.ImplicitTimesFirstChildExpressionContext,
            is ExpressionParser.ImplicitTimesChildExpressionContext,
            is ExpressionParser.PowerChildExpressionContext,
            is ExpressionParser.UnaryMinusChildExpressionContext,
            is ExpressionParser.DegreeChildExpressionContext,
            is ExpressionParser.FunctionExpressionContext,
            is ExpressionParser.ValueContext,
            ->
                transform(tree.getChild(0)!!)

            else -> throw UnsupportedOperationException("Unknown tree node: $tree (type ${tree.javaClass})")
        }
    }

    private fun parseReal(token: Token): Real {
        try {
            return realFormat.parse(token.text ?: "")
        } catch (e: ParseException) {
            // Rethrowing with the right index for the full input string
            throw ParseException(
                message = "Failure parsing number",
                errorOffset = token.startIndex,
                cause = e,
            )
        }
    }

    private fun signFromToken(token: Token?): Real =
        if (token?.type == ExpressionLexer.Tokens.MINUS) Real.MINUS_ONE else Real.ONE

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
            msg: String,
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
                stop = recognizer.inputStream.index()
            }
            this.line = line
            message = msg
        }

        override fun toString(): String {
            return "$start-$stop l.$line: $message"
        }
    }
}
