package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.math.complex.Complex
import garden.ephemeral.math.complex.acos
import garden.ephemeral.math.complex.acosh
import garden.ephemeral.math.complex.acot
import garden.ephemeral.math.complex.acoth
import garden.ephemeral.math.complex.acsc
import garden.ephemeral.math.complex.acsch
import garden.ephemeral.math.complex.asec
import garden.ephemeral.math.complex.asech
import garden.ephemeral.math.complex.asin
import garden.ephemeral.math.complex.asinh
import garden.ephemeral.math.complex.atan
import garden.ephemeral.math.complex.atanh
import garden.ephemeral.math.complex.cos
import garden.ephemeral.math.complex.cosh
import garden.ephemeral.math.complex.cot
import garden.ephemeral.math.complex.coth
import garden.ephemeral.math.complex.csc
import garden.ephemeral.math.complex.csch
import garden.ephemeral.math.complex.exp
import garden.ephemeral.math.complex.ln
import garden.ephemeral.math.complex.sec
import garden.ephemeral.math.complex.sech
import garden.ephemeral.math.complex.sin
import garden.ephemeral.math.complex.sinh
import garden.ephemeral.math.complex.sqrt
import garden.ephemeral.math.complex.tan
import garden.ephemeral.math.complex.tanh
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.acosh
import kotlin.math.asin
import kotlin.math.asinh
import kotlin.math.atan
import kotlin.math.atanh
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.tan
import kotlin.math.tanh

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
    ABS("abs", ::abs, Complex::norm),
    ARG("arg", { 0.0 }, Complex::argument),
    SGN("sgn", { x -> sign(x) }, { z -> z / z.norm }),
    RE("Re", { x -> x }, Complex::real),
    IM("Im", { 0.0 }, Complex::imaginary),
    CONJ("conj", { x -> x }, Complex::conjugate),
    ;

    fun apply(value: Any): Any {
        return when (value) {
            is Double -> realFunction(value)
            is Complex -> complexFunction(value)
            else -> throw IllegalStateException("Cannot apply function $name to value $value")
        }
    }

    companion object {
        private val byName = entries
            .asSequence()
            .map { f -> f.printedName to f }
            .toMap()

        fun findByName(name: String): Function1? = byName[name]
    }
}
