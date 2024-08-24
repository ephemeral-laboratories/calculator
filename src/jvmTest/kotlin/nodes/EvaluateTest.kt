package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.complex.i
import garden.ephemeral.calculator.complex.plus
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
import garden.ephemeral.calculator.nodes.values.Degrees
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.calculator.util.row
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData

class EvaluateTest : FreeSpec({
    "evaluate" - {
        withData(
            row(Value(42.0), Value(42.0)),

            row(
                InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.i)),
                Value(1 + 2.i),
            ),

            row(ConstantNode(Constant.TAU), Value(6.283185307179586)),

            row(PrefixOperatorNode(PrefixOperator.UNARY_MINUS, Value(2.0)), Value(-2.0)),

            row(InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0)), Value(3.0)),
            row(InfixOperatorNode(InfixOperator.MINUS, Value(1.0), Value(2.0)), Value(-1.0)),
            row(InfixOperatorNode(InfixOperator.TIMES, Value(1.0), Value(2.0)), Value(2.0)),
            row(InfixOperatorNode(InfixOperator.DIVIDE, Value(1.0), Value(2.0)), Value(0.5)),

            row(
                InfixOperatorNode(
                    InfixOperator.PLUS,
                    Value(1.0),
                    InfixOperatorNode(InfixOperator.TIMES, Value(2.0), Value(3.0)),
                ),
                Value(7.0),
            ),
            row(
                InfixOperatorNode(
                    InfixOperator.TIMES,
                    InfixOperatorNode(InfixOperator.PLUS, Value(1.0), Value(2.0)),
                    Value(3.0),
                ),
                Value(9.0),
            ),

            row(Function1Node(Function1.SIN, Value(1.0)), Value(0.8414709848078965)),
            row(Function2Node(Function2.POW, Value(2.0), Value(3.0)), Value(8.0)),

            row(
                Function1Node(Function1.SIN, Value(1 + 1.i)),
                Value(1.2984575814159773 + 0.6349639147847361.i),
            ),
            row(Function2Node(Function2.POW, Value(1 + 1.i), Value(2.0)), Value(2.i)),

            row(Degrees(Value(30.0)), Value(0.5235987755982988)),
        ) { (input, expected) ->
            val result = input.evaluate()
            result shouldBeCloseTo expected
        }
    }
})
