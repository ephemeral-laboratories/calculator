package garden.ephemeral.calculator.text

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.math.complex.Complex
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class ComplexFormatTest {
    @ParameterizedTest
    @MethodSource("examples")
    fun format(value: Complex, expected: String) {
        val formatSymbols = PositionalFormatSymbols()
        val realFormat = PositionalFormat(12, formatSymbols)
        val complexFormat = ComplexFormat(realFormat, formatSymbols)
        val result = complexFormat.format(value)
        assertThat(result).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun examples() = listOf(
            arguments(Complex(Double.NaN, Double.NaN), "NaN"),
            arguments(Complex(0.0, Double.POSITIVE_INFINITY), "∞"),
            arguments(Complex(Double.NaN, Double.POSITIVE_INFINITY), "∞"),
            arguments(Complex(Double.NEGATIVE_INFINITY, 0.0), "∞"),
            arguments(Complex(Double.NEGATIVE_INFINITY, Double.NaN), "∞"),
            arguments(Complex(Double.NaN, 0.0), "NaN"),
            arguments(Complex(0.0, Double.NaN), "NaN"),
            arguments(Complex(0.0, 0.0), "0"),
            arguments(Complex(2.5, 0.0), "2;6"),
            arguments(Complex(-2.5, 0.0), "-2;6"),
            arguments(Complex(0.0, 2.5), "2;6i"),
            arguments(Complex(0.0, -2.5), "-2;6i"),
            arguments(Complex(1.5, 2.5), "1;6 + 2;6i"),
            arguments(Complex(1.5, -2.5), "1;6 - 2;6i"),
            arguments(Complex(-1.5, 2.5), "-1;6 + 2;6i"),
            arguments(Complex(-1.5, -2.5), "-1;6 - 2;6i"),
        )
    }
}
