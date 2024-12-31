package garden.ephemeral.calculator.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State

private class MutableStateAdapter<T>(
    private val state: State<T>,
    private val mutate: (T) -> Unit
) : MutableState<T> {
    override var value: T
        get() = state.value
        set(value) {
            mutate(value)
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}

/**
 * Adapts a non-mutable `State<T>` to a `MutableState<T>` by providing a function to call to
 * mutate the value.
 *
 * @receiver the non-mutable state.
 * @param mutate the function to mutate the value.
 * @return the mutable state.
 */
fun <T> State<T>.asMutableState(mutate: (T) -> Unit): MutableState<T> = MutableStateAdapter(this, mutate)
