package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class ComplexFormatTest {
    @ParameterizedTest
    @MethodSource("examples")
    fun format(real: Double, imag: Double, expected: String) {
        val value = Complex(Real.valueOf(real), Real.valueOf(imag))
        val formatSymbols = PositionalFormatSymbols()
        val realFormat = PositionalFormat(12, formatSymbols)
        val complexFormat = ComplexFormat(realFormat, formatSymbols)
        val result = complexFormat.format(value)
        result shouldBe expected
    }

    companion object {
        @JvmStatic
        fun examples() = listOf(
            arguments(0.0, 0.0, "0"),
            arguments(2.5, 0.0, "2;6"),
            arguments(-2.5, 0.0, "-2;6"),
            arguments(0.0, 2.5, "2;6i"),
            arguments(0.0, -2.5, "-2;6i"),
            arguments(1.5, 2.5, "1;6 + 2;6i"),
            arguments(1.5, -2.5, "1;6 - 2;6i"),
            arguments(-1.5, 2.5, "-1;6 + 2;6i"),
            arguments(-1.5, -2.5, "-1;6 - 2;6i"),
        )
    }
}
