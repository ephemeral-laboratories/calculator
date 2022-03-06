package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.math.complex.*
import kotlin.math.*

enum class Function1(
    val printedName: String,
    val realFunction: (Double) -> Any,
    val complexFunction: (Complex) -> Any,
) {
    SIN("sin", ::sin, ::sin),
    COS("cos", ::cos, ::cos),
    TAN("tan", ::tan, ::tan),
    SEC("sec", ::sec, ::sec),
    CSC("csc", ::csc, ::csc),
    COT("cot", ::cot, ::cot),
    SINH("sinh", ::sinh, ::sinh),
    COSH("cosh", ::cosh, ::cosh),
    TANH("tanh", ::tanh, ::tanh),
    SECH("sech", ::sech, ::sech),
    CSCH("csch", ::csch, ::csch),
    COTH("coth", ::coth, ::coth),
    ASIN("asin", ::asin, ::asin),
    ACOS("acos", ::acos, ::acos),
    ATAN("atan", ::atan, ::atan),
    ASEC("asec", ::asec, ::asec),
    ACSC("acsc", ::acsc, ::acsc),
    ACOT("acot", ::acot, ::acot),
    ASINH("asinh", ::asinh, ::asinh),
    ACOSH("acosh", ::acosh, ::acosh),
    ATANH("atanh", ::atanh, ::atanh),
    ASECH("asech", ::asech, ::asech),
    ACSCH("acsch", ::acsch, ::acsch),
    ACOTH("acoth", ::acoth, ::acoth),
    EXP("exp", ::exp, ::exp),
    LOG("log", ::ln, ::ln),
    SQRT("sqrt", ::maybeComplexSqrt, ::sqrt),
    ;

    fun apply(value: Any): Any {
        return when (value) {
            is Double -> realFunction(value)
            is Complex -> complexFunction(value)
            else -> throw IllegalStateException("Cannot apply function $name to value $value")
        }
    }

    companion object {
        private val byName = values()
            .asSequence()
            .map { f -> f.printedName to f }
            .toMap()

        fun findByName(name: String): Function1? = byName[name]
    }
}
