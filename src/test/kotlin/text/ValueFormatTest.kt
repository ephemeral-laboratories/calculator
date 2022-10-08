package garden.ephemeral.calculator.text

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.math.complex.Complex
import org.junit.jupiter.api.Test

class ValueFormatTest {
    @Test
    fun `format real`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format(2.0)
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun `format complex`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format(Complex(1.0, 2.0))
        assertThat(result).isEqualTo("1 + 2i")
    }

    @Test
    fun `format other`() {
        val format = ValueFormat(12, PositionalFormatSymbols())
        val result = format.format("garbage")
        assertThat(result).isEqualTo("garbage")
    }
}
