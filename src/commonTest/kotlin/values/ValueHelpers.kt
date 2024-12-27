package garden.ephemeral.calculator.values

import garden.ephemeral.calculator.creals.Real

// Helper factory method loses precision so it's on the test side
// It's a factory method so starting with uppercase is conventional.
@Suppress("TestFunctionName")
fun Value(value: Double) = Value(Real.valueOf(value))
