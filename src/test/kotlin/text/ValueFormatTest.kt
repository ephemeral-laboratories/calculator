package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ValueFormatTest {
    @Test
    fun `format real`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format(Real.TWO)
        result shouldBe "2"
    }

    @Test
    fun `format complex`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format(Complex(Real.ONE, Real.TWO))
        result shouldBe "1 + 2i"
    }

    @Test
    fun `format other`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format("garbage")
        result shouldBe "garbage"
    }
}
