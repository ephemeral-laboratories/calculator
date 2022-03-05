package garden.ephemeral.calculator.nodes.values

import garden.ephemeral.math.complex.Complex

enum class Constant(val printedName: String, val value: Any) {
    TAU("τ", 2 * kotlin.math.PI),
    PI("π", kotlin.math.PI),
    I("i", Complex.I),
    E("e", kotlin.math.E),
    ;
}