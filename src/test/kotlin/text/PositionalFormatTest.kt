package garden.ephemeral.calculator.text

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.isCloseTo
import garden.ephemeral.calculator.ui.NumberFormatOption
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class PositionalFormatTest {
    @ParameterizedTest
    @MethodSource("parseExamples")
    fun parse(example: String, expected: String) {
        val format = newPositionalFormat()
        val number = format.parse(example)
        assertThat(number).isInstanceOf(Real::class).isCloseTo(expected)
    }

    @ParameterizedTest
    @MethodSource("formatExamples")
    fun format(example: String, expected: String) {
        val format = newPositionalFormat()
        val result = format.format(Real.valueOf(example))
        assertThat(result).isEqualTo(expected)
    }

    private fun newPositionalFormat() =
        PositionalFormat(10, NumberFormatOption.DECIMAL.defaultSymbols)

    companion object {
        @JvmStatic
        fun parseExamples(): List<Arguments> = listOf(
            arguments("0", "0.00000000000000000000"),
            arguments("0.0", "0.00000000000000000000"),
            arguments(".0", "0.00000000000000000000"),
            arguments("0.5", "0.50000000000000000000"),
            arguments(".5", "0.50000000000000000000"),
            arguments("1", "1.00000000000000000000"),
            arguments("1.0", "1.00000000000000000000"),
            arguments("1.00", "1.00000000000000000000"),
            arguments("1.000", "1.00000000000000000000"),
            arguments("1.2", "1.20000000000000000000"),
            arguments("1.20", "1.20000000000000000000"),
            arguments("1.23", "1.23000000000000000000"),
            arguments("1.234", "1.23400000000000000000"),
            arguments("-0.5", "-0.50000000000000000000"),
            arguments("-.5", "-0.50000000000000000000"),
            arguments("-1", "-1.00000000000000000000"),
            arguments("-1.0", "-1.00000000000000000000"),
            arguments("-1.00", "-1.00000000000000000000"),
            arguments("-1.000", "-1.00000000000000000000"),
            arguments("-1.2", "-1.20000000000000000000"),
            arguments("-1.20", "-1.20000000000000000000"),
            arguments("-1.23", "-1.23000000000000000000"),
            arguments("-1.234", "-1.23400000000000000000"),
            arguments("+1.23", "1.23000000000000000000"),
            arguments("1000000000000000000000000", "1000000000000000000000000.00000000000000000000"),
            arguments("1000000000000000000000000000000000000000", "1000000000000000000000000000000000000000.00000000000000000000"),
            arguments("10000000000000000000000000000000000000000", "10000000000000000000000000000000000000000.00000000000000000000"),
        )

        @JvmStatic
        fun formatExamples(): List<Arguments> = listOf(
            arguments("0.0", "0"),
            arguments("0.5", "0.5"),
            arguments("0.99999", "1"),
            arguments("1.0", "1"),
            arguments("1.2", "1.2"),
            arguments("1.23", "1.23"),
            arguments("1.234", "1.234"),
            arguments("1.2345", "1.234"),
            arguments("1.2355", "1.236"),
            arguments("1.99999", "2"),
            arguments("-0.5", "-0.5"),
            arguments("-0.99999", "-1"),
            arguments("-1.0", "-1"),
            arguments("-1.2", "-1.2"),
            arguments("-1.23", "-1.23"),
            arguments("-1.234", "-1.234"),
            arguments("-1.2345", "-1.234"),
            arguments("-1.2355", "-1.236"),
            arguments("-1.99999", "-2"),
            arguments("1000000000000000000000000.00000000000000000000", "1000000000000000000000000"),
            arguments("1000000000000000000000000000000000000000", "1000000000000000000000000000000000000000"),
            arguments("10000000000000000000000000000000000000000", "1E40"),
        )
    }
}
