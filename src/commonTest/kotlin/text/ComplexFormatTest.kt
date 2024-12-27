package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.util.row
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ComplexFormatTest : FreeSpec({
    "format" - {
        withData(
            row(0.0, 0.0, "0"),
            row(2.5, 0.0, "2;6"),
            row(-2.5, 0.0, "-2;6"),
            row(0.0, 2.5, "2;6i"),
            row(0.0, -2.5, "-2;6i"),
            row(1.5, 2.5, "1;6 + 2;6i"),
            row(1.5, -2.5, "1;6 - 2;6i"),
            row(-1.5, 2.5, "-1;6 + 2;6i"),
            row(-1.5, -2.5, "-1;6 - 2;6i"),
        ) { (real, imag, expected) ->
            val value = Complex(Real.valueOf(real), Real.valueOf(imag))
            val formatSymbols = PositionalFormatSymbols()
            val realFormat = PositionalFormat(12, formatSymbols)
            val complexFormat = ComplexFormat(realFormat, formatSymbols)
            val result = complexFormat.format(value)
            result shouldBe expected
        }
    }
})
