package garden.ephemeral.calculator.text

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.creals.Real
import org.junit.jupiter.api.Test

class ValueFormatTest {
    @Test
    fun `format real`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format(Real.TWO)
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun `format complex`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format(Complex(Real.ONE, Real.TWO))
        assertThat(result).isEqualTo("1 + 2i")
    }

    @Test
    fun `format other`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format("garbage")
        assertThat(result).isEqualTo("garbage")
    }
}
