package garden.ephemeral.calculator.text

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.Parentheses
import garden.ephemeral.calculator.nodes.functions.Function1
import garden.ephemeral.calculator.nodes.functions.Function1Node
import garden.ephemeral.calculator.nodes.functions.Function2
import garden.ephemeral.calculator.nodes.functions.Function2Node
import garden.ephemeral.calculator.nodes.isCloseTo
import garden.ephemeral.calculator.nodes.ops.InfixOperator
import garden.ephemeral.calculator.nodes.ops.InfixOperatorNode
import garden.ephemeral.calculator.nodes.ops.PrefixOperator
import garden.ephemeral.calculator.nodes.ops.PrefixOperatorNode
import garden.ephemeral.calculator.nodes.values.Constant
import garden.ephemeral.calculator.nodes.values.ConstantNode
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.math.complex.i
import garden.ephemeral.math.complex.minus
import garden.ephemeral.math.complex.plus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.text.ParseException

class ExpressionParserTest {
    lateinit var parser: ExpressionParser

    @BeforeEach
    fun setUp() {
        parser = ExpressionParser(
            PositionalFormat(12, PositionalFormatSymbols()).apply {
                minimumIntegerDigits = 1
                minimumFractionDigits = 0
                maximumFractionDigits = 10
            }
        )
    }

    @Test
    fun `invalid input`() {
        assertThat {
            parser.parse("2f")
        }.isFailure().isInstanceOf(ParseException::class)
            .prop(ParseException::getErrorOffset).isEqualTo(1)
    }

    @Test
    fun `excessively long number input`() {
        assertThat {
            parser.parse("3 + 1000000000000000000000000")
        }.isFailure().isInstanceOf(ParseException::class)
            .prop(ParseException::getErrorOffset).isEqualTo(4)
    }

    @ParameterizedTest
    @MethodSource("examples", "function1Examples", "function1ComplexExamples", "function2Examples")
    fun `unified test`(input: String, expected: Node) {
        val result = parser.parse(input)
        assertThat(result).isCloseTo(expected)
    }

    companion object {
        @JvmStatic
        fun examples(): List<Arguments> {
            return listOf(
                // Basic value parsing
                arguments("42", Value(50.0)),
                arguments("↊↋", Value(131.0)),
                arguments("123;45", Value(171.36805555555557)),
                arguments(";53", Value(0.43749999999999994)),

                // Constants
                arguments("τ", ConstantNode(Constant.TAU)),
                arguments("tau", ConstantNode(Constant.TAU)),
                arguments("π", ConstantNode(Constant.PI)),
                arguments("pi", ConstantNode(Constant.PI)),
                arguments("e", ConstantNode(Constant.E)),

                // Unary operators
                arguments(
                    "-(3+5)",
                    PrefixOperatorNode(
                        PrefixOperator.UNARY_MINUS,
                        Parentheses(InfixOperatorNode(InfixOperator.PLUS, Value(3.0), Value(5.0)))
                    )
                ),

                // Real value simplification
                arguments("-2", Value(-2.0)),
                arguments("−2", Value(-2.0)),

                // Binary operators
                arguments("1+2", InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0))),
                arguments("1-2", InfixOperatorNode(InfixOperator.MINUS, Value(1.0), Value(2.0))),
                arguments("1−2", InfixOperatorNode(InfixOperator.MINUS, Value(1.0), Value(2.0))),
                arguments("1*2", InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0))),
                arguments("1×2", InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0))),
                arguments("1/2", InfixOperatorNode(InfixOperator.DIVIDE, Value(1.0), Value(2.0))),
                arguments("1÷2", InfixOperatorNode(InfixOperator.DIVIDE, Value(1.0), Value(2.0))),
                arguments("2^2", InfixOperatorNode(InfixOperator.POWER, Value(2.0), Value(2.0))),

                // Binary operators but the cases which are unambiguous if you have more than 2
                arguments(
                    "1+2+3",
                    InfixOperatorNode(
                        InfixOperator.PLUS,
                        InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0)),
                        Value(3.0)
                    )
                ),
                arguments(
                    "1*2*3",
                    InfixOperatorNode(
                        InfixOperator.TIMES,
                        InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0)),
                        Value(3.0)
                    )
                ),
                arguments(
                    "1×2×3",
                    InfixOperatorNode(
                        InfixOperator.TIMES,
                        InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0)),
                        Value(3.0)
                    )
                ),
                arguments(
                    "2πe",
                    InfixOperatorNode(
                        InfixOperator.IMPLICIT_TIMES,
                        InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, Value(2.0), ConstantNode(Constant.PI)),
                        ConstantNode(Constant.E)
                    )
                ),

                // Operator precedence
                arguments(
                    "1+2-3",
                    InfixOperatorNode(
                        InfixOperator.MINUS,
                        InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0)),
                        Value(3.0)
                    )
                ),
                arguments(
                    "1+2*3",
                    InfixOperatorNode(
                        InfixOperator.PLUS,
                        Value(1.0),
                        InfixOperatorNode(InfixOperator.TIMES, Value(2.0), Value(3.0))
                    )
                ),
                arguments(
                    "(1+2)*3",
                    InfixOperatorNode(
                        InfixOperator.TIMES,
                        Parentheses(InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0))),
                        Value(3.0)
                    )
                ),

                // Implicit operators
                arguments(
                    "2π",
                    InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, Value(2.0), ConstantNode(Constant.PI))
                ),

                // Complex value simplification
                arguments("i", Value(1.i)),
                arguments("2i", Value(2.i)),
                arguments("1 + 2i", Value(1 + 2.i)),
                arguments("1 - 2i", Value(1 - 2.i)),
                arguments("-2i", Value((-2).i)),
            )
        }

        @JvmStatic
        fun function1Examples(): List<Arguments> {
            return mapOf(
                "sin" to Function1.SIN,
                "cos" to Function1.COS,
                "tan" to Function1.TAN,
                "sec" to Function1.SEC,
                "csc" to Function1.CSC,
                "cot" to Function1.COT,
                "asin" to Function1.ASIN,
                "acos" to Function1.ACOS,
                "atan" to Function1.ATAN,
                "asec" to Function1.ASEC,
                "acsc" to Function1.ACSC,
                "acot" to Function1.ACOT,
                "sinh" to Function1.SINH,
                "cosh" to Function1.COSH,
                "tanh" to Function1.TANH,
                "sech" to Function1.SECH,
                "csch" to Function1.CSCH,
                "coth" to Function1.COTH,
                "asinh" to Function1.ASINH,
                "acosh" to Function1.ACOSH,
                "atanh" to Function1.ATANH,
                "asech" to Function1.ASECH,
                "acsch" to Function1.ACSCH,
                "acoth" to Function1.ACOTH,
                "exp" to Function1.EXP,
                "log" to Function1.LOG,
                "sqrt" to Function1.SQRT,
            ).map { (name, function) ->
                arguments("$name(1)", Function1Node(function, Value(1.0)))
            }
        }

        @JvmStatic
        fun function1ComplexExamples(): List<Arguments> {
            return mapOf(
                "abs" to Function1.ABS,
                "arg" to Function1.ARG,
                "Re" to Function1.RE,
                "Im" to Function1.IM,
            ).map { (name, function) ->
                arguments("$name(2 + 3i)", Function1Node(function, Value(2 + 3.i)))
            }
        }

        @JvmStatic
        fun function2Examples(): List<Arguments> {
            return mapOf(
                "pow" to Function2.POW,
            ).map { (name, function) ->
                arguments("$name(1, 2)", Function2Node(function, Value(1.0), Value(2.0)))
            }
        }
    }
}
