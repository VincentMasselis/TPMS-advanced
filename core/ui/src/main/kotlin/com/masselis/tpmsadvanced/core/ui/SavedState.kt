package com.masselis.tpmsadvanced.core.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Ready
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Uninitialized
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public fun <T : Any?> saveable(default: () -> T): ReadWriteProperty<SavedStateRegistryOwner, T> =
    StateSaver(default)

private class StateSaver<T : Any?>(
    private val default: () -> T,
) : ReadWriteProperty<SavedStateRegistryOwner, T>, SavedStateRegistry.SavedStateProvider {

    // Thread safety is mandatory because a `SavedStateRegistry` doesn't support to be registered
    // twice with the same key
    private val lock = ReentrantLock()
    private var state: State = Uninitialized

    private sealed interface State {
        data object Uninitialized : State

        @JvmInline
        value class Ready(val value: Any?) : State
    }

    override fun getValue(
        thisRef: SavedStateRegistryOwner,
        property: KProperty<*>
    ): T = lock.withLock {
        registry.register(thisRef, property)
        @Suppress("DEPRECATION")
        when (val state = state) {

            is Ready ->
                // Trust the Ready class, each instantiation of this class uses an instance which
                // matches T
                @Suppress("UNCHECKED_CAST")
                state.value as T

            Uninitialized -> registry
                .consume(thisRef, property)
                ?.get(KEY)
                ?.let {
                    // Trust the Bundle class, the returned type must be same than the type sent
                    // during "saveState()"
                    @Suppress("UNCHECKED_CAST")
                    it as T
                }
                .let { it ?: default() }
                .also { this.state = Ready(it) }
        }
    }

    override fun setValue(
        thisRef: SavedStateRegistryOwner,
        property: KProperty<*>,
        value: T
    ) = lock.withLock {
        registry.register(thisRef, property)
        state = Ready(value)
    }

    @Suppress("MaxLineLength")
    override fun saveState(): Bundle = lock.withLock {
        when (val state = state) {
            Uninitialized -> error("saveState() cannot be called with the state set to \"Uninitialized\" because each time \"registry.register()\" is called, the state is updated to \"Ready\" right after")
            is Ready -> bundleOf(KEY to state.value)
        }
    }

    private val registry = object {
        private fun key(property: KProperty<*>) = "saveable_" + property.name

        fun register(registry: SavedStateRegistryOwner, property: KProperty<*>) {
            val keyName = key(property)
            if (registry.savedStateRegistry.getSavedStateProvider(keyName) !== this@StateSaver)
                registry.savedStateRegistry.registerSavedStateProvider(keyName, this@StateSaver)
        }

        fun consume(registry: SavedStateRegistryOwner, property: KProperty<*>): Bundle? = registry
            .savedStateRegistry
            .consumeRestoredStateForKey(key(property))
    }

    companion object {
        private const val KEY = "VALUE_KEY"
    }
}
