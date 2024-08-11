package garden.ephemeral.calculator.ui

import garden.ephemeral.calculator.nodes.Node
import garden.ephemeral.calculator.nodes.values.Value
import java.util.UUID

data class HistoryEntry(val id: UUID = UUID.randomUUID(), val input: Node, val output: Value)
