package garden.ephemeral.calculator.util

import io.kotest.datatest.IsStableType

fun <A> row(a: A) = Row1(a)
fun <A, B> row(a: A, b: B) = Row2(a, b)
fun <A, B, C> row(a: A, b: B, c: C) = Row3(a, b, c)
fun <A, B, C, D> row(a: A, b: B, c: C, d: D) = Row4(a, b, c, d)

@IsStableType
data class Row1<out A>(val a: A)

@IsStableType
data class Row2<out A, out B>(val a: A, val b: B)

@IsStableType
data class Row3<out A, out B, out C>(val a: A, val b: B, val c: C)

@IsStableType
data class Row4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D)
