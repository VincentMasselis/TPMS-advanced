package com.masselis.tpmsadvanced.core.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Ready
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Saved
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Uninitialized
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Valuable
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public fun <T : Any?> saveable(
    lock: Any? = null,
    default: () -> T
): ReadWriteProperty<SavedStateRegistryOwner, T> = StateSaver(lock, default)

private class StateSaver<T : Any?>(
    lock: Any?,
    private val default: () -> T,
) : ReadWriteProperty<SavedStateRegistryOwner, T>, SavedStateRegistry.SavedStateProvider {

    private val lock = lock ?: this
    private var state: State = Uninitialized

    private sealed interface State {
        data object Uninitialized : State

        sealed interface Valuable : State {
            val value: Any?
        }

        @JvmInline
        value class Ready(override val value: Any?) : Valuable

        @JvmInline
        value class Saved(override val value: Any?) : Valuable
    }

    override fun getValue(
        thisRef: SavedStateRegistryOwner,
        property: KProperty<*>
    ): T = synchronized(lock) {
        registry.register(thisRef, property)
        @Suppress("DEPRECATION")
        when (val state = state) {

            is Valuable ->
                // Trust the Ready or Saved class, each instantiation of this class uses an instance
                // which matches T
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

    @Suppress("MaxLineLength")
    override fun setValue(
        thisRef: SavedStateRegistryOwner,
        property: KProperty<*>,
        value: T
    ) = synchronized(lock) {
        registry.register(thisRef, property)
        state = when (state) {
            Uninitialized, is Ready -> Ready(value)
            is Saved -> error("Cannot set a new value for ${property.name} because the parcelable data was already saved")
        }
    }

    @Suppress("MaxLineLength")
    override fun saveState(): Bundle = synchronized(lock) {
        when (val state = state) {
            is Ready -> {
                this.state = Saved(state.value)
                bundleOf(KEY to state.value)
            }

            Uninitialized -> error("saveState() cannot be called with the state set to \"Uninitialized\" because each time \"registry.register()\" is called, the state is updated to \"Ready\" right after")
            is Saved -> error("saveState() cannot be called twice, state is already set to \"Saved\"")
        }
    }

    private val registry = object {
        private val alreadyRegistered = AtomicBoolean(false)
        private fun key(property: KProperty<*>) = "saveable_" + property.name

        fun register(registry: SavedStateRegistryOwner, property: KProperty<*>) {
            if (alreadyRegistered.compareAndSet(false, true))
                registry.savedStateRegistry.registerSavedStateProvider(
                    key(property),
                    this@StateSaver
                )
        }

        fun consume(registry: SavedStateRegistryOwner, property: KProperty<*>): Bundle? = registry
            .savedStateRegistry
            .consumeRestoredStateForKey(key(property))
    }

    companion object {
        private const val KEY = "VALUE_KEY"
    }
}
