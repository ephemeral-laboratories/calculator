package garden.ephemeral.calculator.util

import io.kotest.data.Row
import io.kotest.datatest.IsStableType

fun <A> row(a: A) = Row1(a)
fun <A, B> row(a: A, b: B) = Row2(a, b)
fun <A, B, C> row(a: A, b: B, c: C) = Row3(a, b, c)
fun <A, B, C, D> row(a: A, b: B, c: C, d: D) = Row4(a, b, c, d)
fun <A, B, C, D, E> row(a: A, b: B, c: C, d: D, e: E) = Row5(a, b, c, d, e)
fun <A, B, C, D, E, F> row(a: A, b: B, c: C, d: D, e: E, f: F) = Row6(a, b, c, d, e, f)

@IsStableType
data class Row1<out A>(val a: A) : Row {
    override fun values() = listOf(a)
}

@IsStableType
data class Row2<out A, out B>(val a: A, val b: B) : Row {
    override fun values() = listOf(a, b)
}

@IsStableType
data class Row3<out A, out B, out C>(val a: A, val b: B, val c: C) : Row {
    override fun values() = listOf(a, b, c)
}

@IsStableType
data class Row4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) : Row {
    override fun values() = listOf(a, b, c, d)
}

@IsStableType
data class Row5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) : Row {
    override fun values() = listOf(a, b, c, d, e)
}

@IsStableType
data class Row6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) : Row {
    override fun values() = listOf(a, b, c, d, e, f)
}
