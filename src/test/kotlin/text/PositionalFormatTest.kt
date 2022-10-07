package garden.ephemeral.calculator.text

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import garden.ephemeral.calculator.ui.NumberFormatOption
import org.junit.jupiter.api.Test

class PositionalFormatTest {
    @Test
    fun `parse integer`() {
        val format = newPositionalFormat()
        val number = format.parse("1")
        assertThat(number).isEqualTo(1.0)
    }

    @Test
    fun `parse decimal`() {
        val format = newPositionalFormat()
        val number = format.parse("1.2")
        assertThat(number).isInstanceOf(Double::class).isCloseTo(1.2, 0.00000001)
    }

    @Test
    fun `format integer`() {
        val format = newPositionalFormat()
        val result = format.format(1.0)
        assertThat(result).isEqualTo("1")
    }

    @Test
    fun `format decimal`() {
        val format = newPositionalFormat()
        val result = format.format(1.2)
        assertThat(result).isEqualTo("1.2")
    }

    private fun newPositionalFormat() =
        PositionalFormat(10, NumberFormatOption.DECIMAL.defaultSymbols)
}