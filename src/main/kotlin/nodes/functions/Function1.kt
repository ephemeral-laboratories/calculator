package garden.ephemeral.calculator.nodes.functions

import garden.ephemeral.calculator.complex.Complex
import garden.ephemeral.calculator.complex.acos
import garden.ephemeral.calculator.complex.acosh
import garden.ephemeral.calculator.complex.asin
import garden.ephemeral.calculator.complex.asinh
import garden.ephemeral.calculator.complex.atan
import garden.ephemeral.calculator.complex.atanh
import garden.ephemeral.calculator.complex.cos
import garden.ephemeral.calculator.complex.cosh
import garden.ephemeral.calculator.complex.exp
import garden.ephemeral.calculator.complex.ln
import garden.ephemeral.calculator.complex.sin
import garden.ephemeral.calculator.complex.sinh
import garden.ephemeral.calculator.complex.sqrt
import garden.ephemeral.calculator.complex.tan
import garden.ephemeral.calculator.complex.tanh
import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.abs
import garden.ephemeral.calculator.creals.acos
import garden.ephemeral.calculator.creals.acosh
import garden.ephemeral.calculator.creals.asin
import garden.ephemeral.calculator.creals.asinh
import garden.ephemeral.calculator.creals.atan
import garden.ephemeral.calculator.creals.atanh
import garden.ephemeral.calculator.creals.cos
import garden.ephemeral.calculator.creals.cosh
import garden.ephemeral.calculator.creals.exp
import garden.ephemeral.calculator.creals.ln
import garden.ephemeral.calculator.creals.sgn
import garden.ephemeral.calculator.creals.sin
import garden.ephemeral.calculator.creals.sinh
import garden.ephemeral.calculator.creals.tan
import garden.ephemeral.calculator.creals.tanh

enum class Function1(
    val printedName: String,
    val realFunction: (Real) -> Any,
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
    SGN("sgn", ::sgn, { z -> z / z.norm }),
    RE("Re", { x -> x }, Complex::real),
    IM("Im", { 0.0 }, Complex::imag),
    CONJ("conj", { x -> x }, Complex::conjugate),
    ;

    operator fun invoke(value: Any): Any {
        return when (value) {
            is Real -> realFunction(value)
            is Complex -> complexFunction(value)
            else -> throw IllegalStateException("Cannot apply function $name to value $value")
        }
    }

    companion object {
        private val byName = entries.associateBy(Function1::printedName)

        fun findByName(name: String): Function1? = byName[name]
    }
}
