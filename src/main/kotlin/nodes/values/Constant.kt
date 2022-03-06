package garden.ephemeral.calculator.nodes.values

enum class Constant(val printedName: String, val value: Any) {
    TAU("τ", 2 * kotlin.math.PI),
    PI("π", kotlin.math.PI),
    E("e", kotlin.math.E),
    ;
}
