package garden.ephemeral.calculator.creals

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec

class MonotoneDerivativeTest : FreeSpec({
    val zero = Real.valueOf(0)
    val one = Real.valueOf(1)
    val three = Real.valueOf(3)

    "cosine computed as monotone derivative of sine should return known expected values" {
        val cosine = monotoneDerivative(::sin, zero, Real.PI)
        assertSoftly {
            cosine(one) shouldBeCloseTo "0.54030230586813971740"
            cosine(three) shouldBeCloseTo "-0.98999249660044545727"
        }
    }
})
