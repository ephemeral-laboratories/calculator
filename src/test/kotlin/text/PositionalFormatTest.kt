package garden.ephemeral.calculator.text

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import garden.ephemeral.calculator.ui.NumberFormatOption
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class PositionalFormatTest {
    @ParameterizedTest
    @MethodSource("parseExamples")
    fun parse(example: String, expected: Double) {
        val format = newPositionalFormat()
        val number = format.parse(example)
        assertThat(number).isInstanceOf(Double::class).isCloseTo(expected, 0.00000001)
    }

    @ParameterizedTest
    @MethodSource("formatExamples")
    fun format(example: Double, expected: String) {
        val format = newPositionalFormat()
        val result = format.format(example)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `format negative long`() {
        val format = newPositionalFormat()
        val result = format.format(-42L)
        assertThat(result).isEqualTo("-42")
    }

    private fun newPositionalFormat() =
        PositionalFormat(10, NumberFormatOption.DECIMAL.defaultSymbols)

    companion object {
        @JvmStatic
        fun parseExamples(): List<Arguments> = listOf(
            arguments("0", 0.0),
            arguments("0.0", 0.0),
            arguments(".0", 0.0),
            arguments("0.5", 0.5),
            arguments(".5", 0.5),
            arguments("1", 1.0),
            arguments("1.0", 1.0),
            arguments("1.00", 1.0),
            arguments("1.000", 1.0),
            arguments("1.2", 1.2),
            arguments("1.20", 1.2),
            arguments("1.23", 1.23),
            arguments("1.234", 1.234),
            arguments("-0.5", -0.5),
            arguments("-.5", -0.5),
            arguments("-1", -1.0),
            arguments("-1.0", -1.0),
            arguments("-1.00", -1.0),
            arguments("-1.000", -1.0),
            arguments("-1.2", -1.2),
            arguments("-1.20", -1.2),
            arguments("-1.23", -1.23),
            arguments("-1.234", -1.234),
            arguments("+1.23", 1.23),
        )

        @JvmStatic
        fun formatExamples(): List<Arguments> = listOf(
            arguments(Double.NaN, "NaN"),
            arguments(Double.POSITIVE_INFINITY, "∞"),
            arguments(Double.NEGATIVE_INFINITY, "-∞"),
            arguments(0.0, "0"),
            arguments(0.5, "0.5"),
            arguments(1.0, "1"),
            arguments(1.2, "1.2"),
            arguments(1.23, "1.23"),
            arguments(1.234, "1.234"),
            arguments(1.2345, "1.234"),
            arguments(1.2355, "1.236"),
            arguments(-0.5, "-0.5"),
            arguments(-1.0, "-1"),
            arguments(-1.2, "-1.2"),
            arguments(-1.23, "-1.23"),
            arguments(-1.234, "-1.234"),
            arguments(-1.2345, "-1.234"),
            arguments(-1.2355, "-1.236"),
        )
    }
}