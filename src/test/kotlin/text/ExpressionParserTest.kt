package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.nodes.Parentheses
import garden.ephemeral.calculator.nodes.functions.Function1
import garden.ephemeral.calculator.nodes.functions.Function1Node
import garden.ephemeral.calculator.nodes.functions.Function2
import garden.ephemeral.calculator.nodes.functions.Function2Node
import garden.ephemeral.calculator.nodes.operators.InfixOperator
import garden.ephemeral.calculator.nodes.operators.InfixOperatorNode
import garden.ephemeral.calculator.nodes.operators.PrefixOperator
import garden.ephemeral.calculator.nodes.operators.PrefixOperatorNode
import garden.ephemeral.calculator.nodes.shouldBeCloseTo
import garden.ephemeral.calculator.nodes.values.Constant
import garden.ephemeral.calculator.nodes.values.ConstantNode
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.calculator.util.row
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.text.ParseException

class ExpressionParserTest : FreeSpec({
    lateinit var parser: ExpressionParser

    beforeEach {
        parser = ExpressionParser(
            PositionalFormat(12, PositionalFormatSymbols()).apply {
                minimumIntegerDigits = 1
                minimumFractionDigits = 0
                maximumFractionDigits = 10
            },
        )
    }

    "invalid input" {
        shouldThrow<ParseException> {
            parser.parse("2f")
        }.errorOffset shouldBe 1
    }

    val examples = listOf(
        // Basic value parsing
        row("42", Value(50.0)),
        row("↊↋", Value(131.0)),
        row("123;45", Value(171.36805555555557)),
        row(";53", Value(0.43749999999999994)),

        // Constants
        row("τ", ConstantNode(Constant.TAU)),
        row("tau", ConstantNode(Constant.TAU)),
        row("π", ConstantNode(Constant.PI)),
        row("pi", ConstantNode(Constant.PI)),
        row("e", ConstantNode(Constant.E)),

        // Unary operators
        row(
            "-(3+5)",
            PrefixOperatorNode(
                PrefixOperator.UNARY_MINUS,
                Parentheses(InfixOperatorNode(InfixOperator.PLUS, Value(3.0), Value(5.0))),
            ),
        ),

        // Real value simplification
        row("-2", Value(-2.0)),
        row("−2", Value(-2.0)),

        // Binary operators
        row("1+2", InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0))),
        row("1-2", InfixOperatorNode(InfixOperator.MINUS, Value(1.0), Value(2.0))),
        row("1−2", InfixOperatorNode(InfixOperator.MINUS, Value(1.0), Value(2.0))),
        row("1*2", InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0))),
        row("1×2", InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0))),
        row("1/2", InfixOperatorNode(InfixOperator.DIVIDE, Value(1.0), Value(2.0))),
        row("1÷2", InfixOperatorNode(InfixOperator.DIVIDE, Value(1.0), Value(2.0))),
        row("2^2", InfixOperatorNode(InfixOperator.POWER, Value(2.0), Value(2.0))),

        // Binary operators but the cases which are unambiguous if you have more than 2
        row(
            "1+2+3",
            InfixOperatorNode(
                InfixOperator.PLUS,
                InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0)),
                Value(3.0),
            ),
        ),
        row(
            "1*2*3",
            InfixOperatorNode(
                InfixOperator.TIMES,
                InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0)),
                Value(3.0),
            ),
        ),
        row(
            "1×2×3",
            InfixOperatorNode(
                InfixOperator.TIMES,
                InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0)),
                Value(3.0),
            ),
        ),
        row(
            "2πe",
            InfixOperatorNode(
                InfixOperator.IMPLICIT_TIMES,
                InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, Value(2.0), ConstantNode(Constant.PI)),
                ConstantNode(Constant.E),
            ),
        ),

        // Implicit operators
        row(
            "2π",
            InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, Value(2.0), ConstantNode(Constant.PI)),
        ),
        row(
            "2(3)",
            InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, Value(2.0), Parentheses(Value(3.0))),
        ),
        row(
            "2(1+2)",
            InfixOperatorNode(
                InfixOperator.IMPLICIT_TIMES,
                Value(2.0),
                Parentheses(InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0))),
            ),
        ),

        // Operator precedence
        row(
            "1+2-3",
            InfixOperatorNode(
                InfixOperator.MINUS,
                InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0)),
                Value(3.0),
            ),
        ),
        row(
            "3-1+2",
            InfixOperatorNode(
                InfixOperator.PLUS,
                InfixOperatorNode(InfixOperator.MINUS, Value(3.0), Value(1.0)),
                Value(2.0),
            ),
        ),
        row(
            "1+2*3",
            InfixOperatorNode(
                InfixOperator.PLUS,
                Value(1.0),
                InfixOperatorNode(InfixOperator.TIMES, Value(2.0), Value(3.0)),
            ),
        ),
        row(
            "(1+2)*3",
            InfixOperatorNode(
                InfixOperator.TIMES,
                Parentheses(InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0))),
                Value(3.0),
            ),
        ),
        row(
            "1*2/3",
            InfixOperatorNode(
                InfixOperator.DIVIDE,
                InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0)),
                Value(3.0),
            ),
        ),
        row(
            "1/2*3",
            InfixOperatorNode(
                InfixOperator.TIMES,
                InfixOperatorNode(InfixOperator.DIVIDE, Value(1.0), Value(2.0)),
                Value(3.0),
            ),
        ),
        row(
            "6/2(3)",
            InfixOperatorNode(
                InfixOperator.DIVIDE,
                Value(6.0),
                InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, Value(2.0), Parentheses(Value(3.0))),
            ),
        ),
        row(
            "3*1(2)",
            InfixOperatorNode(
                InfixOperator.TIMES,
                Value(3.0),
                InfixOperatorNode(InfixOperator.IMPLICIT_TIMES, Value(1.0), Parentheses(Value(2.0))),
            ),
        ),

        // Complex value simplification
        row("i", Value(Complex.I)),
        row("2i", Value(Complex(Real.ZERO, Real.TWO))),
        row("1 + 2i", Value(Complex(Real.ONE, Real.TWO))),
        row("1 - 2i", Value(Complex(Real.ONE, Real.valueOf(-2)))),
        row("-2i", Value(Complex(Real.ZERO, Real.valueOf(-2)))),

        // Long values
        row(
            "3 + 1000000000000000000000000",
            InfixOperatorNode(
                InfixOperator.PLUS,
                Value(Real.valueOf(3)),
                Value(Real.valueOf("79496847203390844133441536")),
            ),
        ),
    )

    val function1Examples = mapOf(
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
        row("$name(1)", Function1Node(function, Value(1.0)))
    }

    val function1ComplexExamples = mapOf(
        "abs" to Function1.ABS,
        "arg" to Function1.ARG,
        "Re" to Function1.RE,
        "Im" to Function1.IM,
    ).map { (name, function) ->
        row("$name(2 + 3i)", Function1Node(function, Value(Complex(Real.TWO, Real.valueOf(3)))))
    }

    val function2Examples = mapOf(
        "pow" to Function2.POW,
    ).map { (name, function) ->
        row("$name(1, 2)", Function2Node(function, Value(Real.ONE), Value(Real.TWO)))
    }

    "parse" - {
        withData(examples + function1Examples + function1ComplexExamples + function2Examples) { (input, expected) ->
            val result = parser.parse(input)
            result shouldBeCloseTo expected
        }
    }
})
