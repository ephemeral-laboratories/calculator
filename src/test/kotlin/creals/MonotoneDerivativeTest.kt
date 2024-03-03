package garden.ephemeral.calculator.creals

import assertk.assertAll
import assertk.assertThat
import org.junit.jupiter.api.Test

class MonotoneDerivativeTest {
    @Test
    fun testMonotoneDerivative() {
        val cosine = monotoneDerivative(::sin, ZERO, Real.PI)
        assertAll {
            assertThat(cosine(ONE)).isCloseTo("0.54030230586813971740")
            assertThat(cosine(THREE)).isCloseTo("-0.98999249660044545727")
        }
    }

    companion object {
        private val ZERO = Real.valueOf(0)
        private val ONE = Real.valueOf(1)
        private val THREE = Real.valueOf(3)
    }
}
