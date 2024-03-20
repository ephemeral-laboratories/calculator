package garden.ephemeral.calculator.creals

import io.kotest.assertions.assertSoftly
import org.junit.jupiter.api.Test

class MonotoneDerivativeTest {
    @Test
    fun testMonotoneDerivative() {
        val cosine = monotoneDerivative(::sin, ZERO, Real.PI)
        assertSoftly {
            cosine(ONE) shouldBeCloseTo "0.54030230586813971740"
            cosine(THREE) shouldBeCloseTo "-0.98999249660044545727"
        }
    }

    companion object {
        private val ZERO = Real.valueOf(0)
        private val ONE = Real.valueOf(1)
        private val THREE = Real.valueOf(3)
    }
}
