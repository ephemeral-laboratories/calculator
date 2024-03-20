package garden.ephemeral.calculator.nodes

import garden.ephemeral.calculator.complex.i
import garden.ephemeral.calculator.complex.minus
import garden.ephemeral.calculator.complex.plus
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.calculator.text.ValueFormat
import garden.ephemeral.calculator.ui.NumberFormatOption
import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class PrettyPrintTest {
    @ParameterizedTest
    @MethodSource("examples")
    fun `unified test`(input: Any, expected: String) {
        val result = Value(input).prettyPrint(ValueFormat(10, NumberFormatOption.DECIMAL.defaultSymbols))
        result shouldBe expected
    }

    companion object {
        @JvmStatic
        fun examples(): List<Arguments> {
            val small = 1.0E-17
            return listOf(
                // Basic real cases
                arguments(0.0, "0"),
                arguments(1.0, "1"),
                arguments(-1.0, "-1"),
                arguments(50.0, "50"),
                arguments(131.0, "131"),
                arguments(171.5, "171.5"),

                // Basic complex cases
                arguments(2.i, "2i"),
                arguments((-2).i, "-2i"),
                arguments(1 + 2.i, "1 + 2i"),
                arguments(1 - 2.i, "1 - 2i"),
                arguments(-1 + 2.i, "-1 + 2i"),

                // Special cases of i with no multiplier
                arguments(1.i, "i"),
                arguments((-1).i, "-i"),
                arguments(1 + 1.i, "1 + i"),
                arguments(1 - 1.i, "1 - i"),

                arguments(small, "0"),
                arguments(small + 2.i, "2i"),
                arguments(small - 2.i, "-2i"),
                arguments(small.i, "0"),
                arguments(-small.i, "0"),
                arguments(2 + small.i, "2"),
                arguments(2 - small.i, "2"),
            )
        }
    }
}
