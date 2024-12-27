package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.complex.i
import garden.ephemeral.calculator.complex.plus
import garden.ephemeral.calculator.functions.Function1
import garden.ephemeral.calculator.functions.Function2
import garden.ephemeral.calculator.nodes.functions.Function1Node
import garden.ephemeral.calculator.nodes.functions.Function2Node
import garden.ephemeral.calculator.nodes.operators.InfixOperatorNode
import garden.ephemeral.calculator.nodes.operators.PostfixOperatorNode
import garden.ephemeral.calculator.nodes.operators.PrefixOperatorNode
import garden.ephemeral.calculator.nodes.values.ConstantNode
import garden.ephemeral.calculator.operators.InfixOperator
import garden.ephemeral.calculator.operators.PostfixOperator
import garden.ephemeral.calculator.operators.PrefixOperator
import garden.ephemeral.calculator.util.row
import garden.ephemeral.calculator.values.Constant
import garden.ephemeral.calculator.values.Value
import garden.ephemeral.calculator.values.shouldBeCloseTo
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData

class EvaluateTest : FreeSpec({

    "evaluate" - {
        withData(
            row(ValueNode(42.0), Value(42.0)),

            row(
                InfixOperatorNode(InfixOperator.PLUS, ValueNode(1.0), ValueNode(2.i)),
                Value(1 + 2.i),
            ),

            row(ConstantNode(Constant.TAU), Value(6.283185307179586)),

            row(PrefixOperatorNode(PrefixOperator.UNARY_MINUS, ValueNode(2.0)), Value(-2.0)),

            row(InfixOperatorNode(InfixOperator.PLUS, ValueNode(1.0), ValueNode(2.0)), Value(3.0)),
            row(InfixOperatorNode(InfixOperator.MINUS, ValueNode(1.0), ValueNode(2.0)), Value(-1.0)),
            row(InfixOperatorNode(InfixOperator.TIMES, ValueNode(1.0), ValueNode(2.0)), Value(2.0)),
            row(InfixOperatorNode(InfixOperator.DIVIDE, ValueNode(1.0), ValueNode(2.0)), Value(0.5)),

            row(
                InfixOperatorNode(
                    InfixOperator.PLUS,
                    ValueNode(1.0),
                    InfixOperatorNode(InfixOperator.TIMES, ValueNode(2.0), ValueNode(3.0)),
                ),
                Value(7.0),
            ),
            row(
                InfixOperatorNode(
                    InfixOperator.TIMES,
                    InfixOperatorNode(InfixOperator.PLUS, ValueNode(1.0), ValueNode(2.0)),
                    ValueNode(3.0),
                ),
                Value(9.0),
            ),

            row(Function1Node(Function1.SIN, ValueNode(1.0)), Value(0.8414709848078965)),
            row(Function2Node(Function2.POW, ValueNode(2.0), ValueNode(3.0)), Value(8.0)),

            row(
                Function1Node(Function1.SIN, ValueNode(1 + 1.i)),
                Value(1.2984575814159773 + 0.6349639147847361.i),
            ),
            row(Function2Node(Function2.POW, ValueNode(1 + 1.i), ValueNode(2.0)), Value(2.i)),

            row(PostfixOperatorNode(PostfixOperator.DEGREES, ValueNode(30.0)), Value(0.5235987755982988)),
        ) { (input, expected) ->
            val result = input.evaluate()
            result shouldBeCloseTo expected
        }
    }
})
