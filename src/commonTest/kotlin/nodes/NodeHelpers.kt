package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.nodes.values.ValueNode
import garden.ephemeral.calculator.values.Value

// Factory methods conventionally start with uppercase
@Suppress("TestFunctionName")
fun ValueNode(value: Real) = ValueNode(Value(value))

@Suppress("TestFunctionName")
fun ValueNode(value: Complex) = ValueNode(Value(value))

@Suppress("TestFunctionName")
fun ValueNode(value: Double) = ValueNode(Value(value))
