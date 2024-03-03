package garden.ephemeral.calculator.complex

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.cos
import garden.ephemeral.calculator.creals.cosh
import garden.ephemeral.calculator.creals.exp
import garden.ephemeral.calculator.creals.ln
import garden.ephemeral.calculator.creals.sin
import garden.ephemeral.calculator.creals.sinh

// e^(a + b i) = e^a . e^(b i)
//             = e^a . (cos(b) + i sin(b))
fun exp(z: Complex) = (Complex.I * sin(z.imag) + cos(z.imag)) * exp(z.real)

fun ln(z: Complex) = Complex(ln(z.squaredNorm) * Real.ONE_HALF, z.argument)

fun sqrt(z: Complex) = z.pow(Real.ONE_HALF)

// Definition of the functions sin, cos, sinh, cosh:
// sin(x) = (exp(x i) - exp(-x i)) / 2                          <- [Equation 1]
// cos(x) = (exp(x i) + exp(-x i)) / 2                          <- [Equation 2]
// sinh(x) = (exp(x) - exp(-x)) / 2                             <- [Equation 3]
// cosh(x) = (exp(x) + exp(-x)) / 2                             <- [Equation 4]

// Can derive these by substituting into 1-4 above:
// sin(b i) = i sinh(b)                                         <- [Equation 5]
// cos(b i) = cosh(b)                                           <- [Equation 6]
// sinh(b i) = i sin(b)                                         <- [Equation 7]
// cosh(b i) = cos(b)                                           <- [Equation 8]

// sin(a + b) = sin(a) cos(b) + cos(a) sin(b)                   <- Sum of angles formula for sin
// sin(a + b i) = sin(a) cos(b i) + cos(a) sin(b i)             <- Expand for a + bi
//              = sin(a) cosh(b) + cos(a) sinh(b) i             <- Simplify using Equations 5, 6
fun sin(z: Complex) = Complex(sin(z.real) * cosh(z.imag), cos(z.real) * sinh(z.imag))

// cos(a + b) = cos(a) cos(b) - sin(a) sin(b)                   <- Sum of angles formula for cos
// cos(a + b i) = cos(a) cos(b i) - sin(a) sin(b i)             <- Expand for a + bi
//              = cos(a) cosh(b) - sin(a) sinh(b) i             <- Simplify using Equations 5, 6
fun cos(z: Complex) = Complex(cos(z.real) * cosh(z.imag), -sin(z.real) * sinh(z.imag))

fun tan(z: Complex) = sin(z) / cos(z)

fun asin(z: Complex) = Complex.MINUS_I * ln(Complex.I * z + sqrt(Complex.ONE - z * z))

fun acos(z: Complex) = Complex.HALF_PI + Complex.I * ln(Complex.I * z + sqrt(Complex.ONE - z * z))

fun atan(z: Complex): Complex {
    val a = Complex.I * z
    return Complex.HALF_I * (ln(Complex.ONE - a) - ln(Complex.ONE + a))
}

// sinh(a + b) = sinh(-(a + b)i i)                              <- Expand 1 to -i * i
//             = i sin(-(a + b)i)                               <- Simplify using Equation 7
//             = i (sin(-ai) cos(-bi) + cos(-ai) sin(-bi))      <- Expand using sum of angles formula for sin
//             = i (i sinh(-a) cosh(-b) + i cosh(-a) sinh(-b))  <- Simplify using Equations 5, 6
//             = - sinh(-a) cosh(-b) - cosh(-a) sinh(-b)        <- Simplify i * i to -1
//             = sinh(a) cosh(b) + cosh(a) sinh(b)              <- Sum of angles formula for sinh
// sinh(a + b i) = sinh(a) cosh(b i) + cosh(a) sinh(b i)        <- Expand for a + bi
//               = sinh(a) cos(b) + cosh(a) sin(b) i            <- Simplify using Equations 7, 8
fun sinh(z: Complex) = Complex(sinh(z.real) * cos(z.imag), cosh(z.real) * sin(z.imag))

// cosh(a + b) = cosh(-(a + b)i i)                              <- Expand 1 to -i * i
//             = cos(-(a + b)i)                                 <- Simplify using Equation 8
//             = cos(-a i) cos(-b i) - sin(-a i) sin(-b i)      <- Expand using sum of angles formula for cos
//             = cosh(-a) cosh(-b) - i i sinh(-a) sinh(-b)      <- Simplify using Equations 5, 6
//             = cosh(-a) cosh(-b) + sinh(-a) sinh(-b)          <- Simplify -i * i to 1
//             = cosh(a) cosh(b) + sinh(a) sinh(b)              <- Sum of angles formula for cosh
// cosh(a + b i) = cosh(a) cosh(b i) + sinh(a) sinh(b i)        <- Expand for a + bi
//               = cosh(a) cos(b) + sinh(a) sin(b) i            <- Simplify using Equations 7, 8
fun cosh(z: Complex) = Complex(cosh(z.real) * cos(z.imag), sinh(z.real) * sin(z.imag))

fun tanh(z: Complex) = sinh(z) / cosh(z)

fun asinh(z: Complex) = ln(sqrt(Complex.ONE + z * z) + z)

fun acosh(z: Complex) = ln(sqrt(z - Complex.ONE) * sqrt(z + Complex.ONE) + z)

fun atanh(z: Complex) = Complex.ONE_HALF * (ln(Complex.ONE + z) - ln(Complex.ONE - z))
