package garden.ephemeral.calculator.functions

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
import garden.ephemeral.calculator.values.Value

enum class Function1(
    val printedName: String,
    val realFunction: (Real) -> Value,
    val complexFunction: (Complex) -> Value,
) {
    SIN("sin", { Value.OfReal(sin(it)) }, { Value.OfComplex(sin(it)) }),
    COS("cos", { Value.OfReal(cos(it)) }, { Value.OfComplex(cos(it)) }),
    TAN("tan", { Value.OfReal(tan(it)) }, { Value.OfComplex(tan(it)) }),
    SEC("sec", { Value.OfReal(sec(it)) }, { Value.OfComplex(sec(it)) }),
    CSC("csc", { Value.OfReal(csc(it)) }, { Value.OfComplex(csc(it)) }),
    COT("cot", { Value.OfReal(cot(it)) }, { Value.OfComplex(cot(it)) }),
    SINH("sinh", { Value.OfReal(sinh(it)) }, { Value.OfComplex(sinh(it)) }),
    COSH("cosh", { Value.OfReal(cosh(it)) }, { Value.OfComplex(cosh(it)) }),
    TANH("tanh", { Value.OfReal(tanh(it)) }, { Value.OfComplex(tanh(it)) }),
    SECH("sech", { Value.OfReal(sech(it)) }, { Value.OfComplex(sech(it)) }),
    CSCH("csch", { Value.OfReal(csch(it)) }, { Value.OfComplex(csch(it)) }),
    COTH("coth", { Value.OfReal(coth(it)) }, { Value.OfComplex(coth(it)) }),
    ASIN("asin", { Value.OfReal(asin(it)) }, { Value.OfComplex(asin(it)) }),
    ACOS("acos", { Value.OfReal(acos(it)) }, { Value.OfComplex(acos(it)) }),
    ATAN("atan", { Value.OfReal(atan(it)) }, { Value.OfComplex(atan(it)) }),
    ASEC("asec", { Value.OfReal(asec(it)) }, { Value.OfComplex(asec(it)) }),
    ACSC("acsc", { Value.OfReal(acsc(it)) }, { Value.OfComplex(acsc(it)) }),
    ACOT("acot", { Value.OfReal(acot(it)) }, { Value.OfComplex(acot(it)) }),
    ASINH("asinh", { Value.OfReal(asinh(it)) }, { Value.OfComplex(asinh(it)) }),
    ACOSH("acosh", { Value.OfReal(acosh(it)) }, { Value.OfComplex(acosh(it)) }),
    ATANH("atanh", { Value.OfReal(atanh(it)) }, { Value.OfComplex(atanh(it)) }),
    ASECH("asech", { Value.OfReal(asech(it)) }, { Value.OfComplex(asech(it)) }),
    ACSCH("acsch", { Value.OfReal(acsch(it)) }, { Value.OfComplex(acsch(it)) }),
    ACOTH("acoth", { Value.OfReal(acoth(it)) }, { Value.OfComplex(acoth(it)) }),
    EXP("exp", { Value.OfReal(exp(it)) }, { Value.OfComplex(exp(it)) }),
    LOG("log", { Value.OfReal(ln(it)) }, { Value.OfComplex(ln(it)) }),
    SQRT("sqrt", { maybeComplexSqrt(it) }, { Value.OfComplex(sqrt(it)) }),
    ABS("abs", { Value.OfReal(abs(it)) }, { Value.OfReal(it.norm) }),
    ARG("arg", { Value.OfReal(Real.ZERO) }, { Value.OfReal(it.argument) }),
    SGN("sgn", { Value.OfReal(sgn(it)) }, { Value.OfComplex(it / it.norm) }),
    RE("Re", { Value.OfReal(it) }, { Value.OfReal(it.real) }),
    IM("Im", { Value.OfReal(Real.ZERO) }, { Value.OfReal(it.imag) }),
    CONJ("conj", { Value.OfReal(it) }, { Value.OfComplex(it.conjugate) }),
    ;

    operator fun invoke(value: Value): Value = when (value) {
        is Value.OfReal -> realFunction(value.value)
        is Value.OfComplex -> complexFunction(value.value)
    }

    companion object {
        private val byName = entries.associateBy(Function1::printedName)

        fun findByName(name: String): Function1? = byName[name]
    }
}
