package garden.ephemeral.calculator.values

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.exp
import garden.ephemeral.calculator.creals.sqrt

enum class Constant(val printedName: String, private val valueFactory: () -> Real) {
    TAU("τ", { Real.TAU }),
    PI("π", { Real.PI }),
    PHI("φ", { (Real.ONE + sqrt(Real.valueOf(5))) / Real.TWO }),
    E("e", { exp(Real.ONE) }),
    ;

    val value: Value by lazy { Value.OfReal(valueFactory()) }
}
