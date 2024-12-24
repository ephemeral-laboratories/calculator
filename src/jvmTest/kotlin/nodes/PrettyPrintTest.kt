package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.complex.i
import garden.ephemeral.calculator.complex.minus
import garden.ephemeral.calculator.complex.plus
import garden.ephemeral.calculator.text.ValueFormat
import garden.ephemeral.calculator.ui.NumberFormatOption
import garden.ephemeral.calculator.util.row
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class PrettyPrintTest : FreeSpec({
    "prettyPrint" - {
        val small = 1.0E-17
        withData(
            // Basic real cases
            row(ValueNode(0.0), "0"),
            row(ValueNode(1.0), "1"),
            row(ValueNode(-1.0), "-1"),
            row(ValueNode(50.0), "50"),
            row(ValueNode(131.0), "131"),
            row(ValueNode(171.5), "171.5"),

            // Basic complex cases
            row(ValueNode(2.i), "2i"),
            row(ValueNode((-2).i), "-2i"),
            row(ValueNode(1 + 2.i), "1 + 2i"),
            row(ValueNode(1 - 2.i), "1 - 2i"),
            row(ValueNode(-1 + 2.i), "-1 + 2i"),

            // Special cases of i with no multiplier
            row(ValueNode(1.i), "i"),
            row(ValueNode((-1).i), "-i"),
            row(ValueNode(1 + 1.i), "1 + i"),
            row(ValueNode(1 - 1.i), "1 - i"),

            row(ValueNode(small), "0"),
            row(ValueNode(small + 2.i), "2i"),
            row(ValueNode(small - 2.i), "-2i"),
            row(ValueNode(small.i), "0"),
            row(ValueNode(-small.i), "0"),
            row(ValueNode(2 + small.i), "2"),
            row(ValueNode(2 - small.i), "2"),
        ) { (input, expected) ->
            val result = input.prettyPrint(ValueFormat(10, NumberFormatOption.DECIMAL.defaultSymbols))
            result shouldBe expected
        }
    }
})
