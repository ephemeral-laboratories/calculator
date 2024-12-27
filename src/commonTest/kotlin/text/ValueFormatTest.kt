package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.values.Value
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ValueFormatTest : FreeSpec({
    "format" - {
        "for real value" {
            val format = ValueFormat(12, PositionalFormatSymbols())
            val result = format.format(Value(Real.TWO))
            result shouldBe "2"
        }

        "for complex value" {
            val format = ValueFormat(12, PositionalFormatSymbols())
            val result = format.format(Value(Complex(Real.ONE, Real.TWO)))
            result shouldBe "1 + 2i"
        }
    }
})
