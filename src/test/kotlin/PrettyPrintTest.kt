package garden.ephemeral.calculator

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.calculator.nodes.values.Value
import garden.ephemeral.calculator.text.NumberFormats
import garden.ephemeral.math.complex.Complex
import garden.ephemeral.math.complex.i
import garden.ephemeral.math.complex.minus
import garden.ephemeral.math.complex.plus
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class PrettyPrintTest {
    @ParameterizedTest
    @MethodSource("examples")
    fun `unified test`(input: Any, expected: String) {
        val result = Value(input).prettyPrint(NumberFormats.decimalFormat)
        assertThat(result).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun examples(): List<Arguments> {
            return listOf(
                arguments(0.0, "0"),
                arguments(50.0, "50"),
                arguments(131.0, "131"),
                arguments(171.5, "171.5"),
                arguments(2.i, "2i"),
                arguments((-2).i, "-2i"),
                arguments(1 + 2.i, "1 + 2i"),
                arguments(1 - 2.i, "1 - 2i"),
                arguments(Double.NaN, "undefined"),
                arguments(Complex(Double.NaN, 1.0), "undefined"),
                arguments(Double.POSITIVE_INFINITY, "∞"),
                arguments(Double.NEGATIVE_INFINITY, "-∞"),
            )
        }
    }
}
