package garden.ephemeral.calculator.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

/**
 * Alternative operator for getting a settings value, adding support for enums.
 *
 * @receiver the settings.
 * @param key the key to look up in the settings.
 * @param defaultValue a value to use if the setting is absent or invalid.
 * @return the enum setting value.
 */
inline operator fun <reified E: Enum<E>> Settings.get(key: String, defaultValue: E): E {
    val stringValue = this[key, defaultValue.name]
    return try {
        enumValueOf<E>(stringValue)
    } catch (e: IllegalArgumentException) {
        defaultValue
    }
}

/**
 * Alternative operator for setting a settings value, adding support for enums.
 *
 * @receiver the settings.
 * @param key the key to look up in the settings.
 * @param value a value to use if the setting is absent or invalid.
 */
inline operator fun <reified E: Enum<E>> Settings.set(key: String, value: E) {
    this[key, value.name]
}

/**
 * Produces a `MutableState<Boolean>` which reflects the current value of a stored setting
 * while also allowing changing that setting.
 *
 * @receiver the settings object.
 * @param key the setting key.
 * @param defaultValue the default value for the setting.
 * @return the state object.
 */
fun Settings.mutableBooleanState(key: String, defaultValue: Boolean): MutableState<Boolean> {
    val basicMutableState = mutableStateOf(this[key, defaultValue])

    // Slightly abuse `asMutableState` to provide a hook to sync the setting.
    return basicMutableState.asMutableState { newValue ->
        this[key] = newValue
        basicMutableState.value = newValue
    }
}

/**
 * Produces a `MutableState<E>` which reflects the current value of a stored setting
 * while also allowing changing that setting.
 *
 * @receiver the settings object.
 * @param key the setting key.
 * @param defaultValue the default value for the setting.
 * @return the state object.
 */
inline fun <reified E: Enum<E>> Settings.mutableEnumState(key: String, defaultValue: E): MutableState<E> {
    val basicMutableState = mutableStateOf(this[key, defaultValue])

    // Slightly abuse `asMutableState` to provide a hook to sync the setting.

    return basicMutableState.asMutableState { newValue ->
        // Had to add `.name` here because otherwise Kotlin calls their `set(String, Any)` method,
        // which rejects enum values.
        this[key] = newValue.name
        basicMutableState.value = newValue
    }
}
