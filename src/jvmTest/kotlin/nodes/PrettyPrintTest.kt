package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.complex.i
import garden.ephemeral.calculator.complex.minus
import garden.ephemeral.calculator.complex.plus
import garden.ephemeral.calculator.nodes.values.Value
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
            row(0.0, "0"),
            row(1.0, "1"),
            row(-1.0, "-1"),
            row(50.0, "50"),
            row(131.0, "131"),
            row(171.5, "171.5"),

            // Basic complex cases
            row(2.i, "2i"),
            row((-2).i, "-2i"),
            row(1 + 2.i, "1 + 2i"),
            row(1 - 2.i, "1 - 2i"),
            row(-1 + 2.i, "-1 + 2i"),

            // Special cases of i with no multiplier
            row(1.i, "i"),
            row((-1).i, "-i"),
            row(1 + 1.i, "1 + i"),
            row(1 - 1.i, "1 - i"),

            row(small, "0"),
            row(small + 2.i, "2i"),
            row(small - 2.i, "-2i"),
            row(small.i, "0"),
            row(-small.i, "0"),
            row(2 + small.i, "2"),
            row(2 - small.i, "2"),
        ) { (input, expected) ->
            val result = Value(input).prettyPrint(ValueFormat(10, NumberFormatOption.DECIMAL.defaultSymbols))
            result shouldBe expected
        }
    }
})
