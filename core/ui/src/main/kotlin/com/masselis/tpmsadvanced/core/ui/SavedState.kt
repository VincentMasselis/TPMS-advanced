package com.masselis.tpmsadvanced.core.ui

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Ready
import com.masselis.tpmsadvanced.core.ui.StateSaver.State.Uninitialized
import com.masselis.tpmsadvanced.core.ui.StateSaver.SupportedTypes.Companion.get
import com.masselis.tpmsadvanced.core.ui.StateSaver.SupportedTypes.Companion.typeForValue
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public fun <T : Any?> saveable(default: () -> T): ReadWriteProperty<SavedStateRegistryOwner, T> =
    StateSaver(default)

private class StateSaver<T : Any?>(
    private val default: () -> T,
) : ReadWriteProperty<SavedStateRegistryOwner, T>, SavedStateRegistry.SavedStateProvider {

    private var state: State = Uninitialized

    private sealed interface State {
        data object Uninitialized : State

        @JvmInline
        value class Ready(val value: Any?) : State
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: SavedStateRegistryOwner, property: KProperty<*>): T {
        registry.register(thisRef, property)
        return when (val state = state) {

            is Ready -> state.value as T

            Uninitialized -> registry
                .consume(thisRef, property)
                ?.let { bundle ->
                    bundle.getInt(TYPE_KEY, -1)
                        .let { typeOrdinal -> SupportedTypes.entries.first { it.ordinal == typeOrdinal } }
                        .let { bundle.get(it, VALUE_KEY) as T }
                }
                .let { it ?: default() }
                .also { this.state = Ready(it) }
        }
    }

    override fun setValue(
        thisRef: SavedStateRegistryOwner,
        property: KProperty<*>,
        value: T
    ) {
        registry.register(thisRef, property)
        state = Ready(value)
    }

    override fun saveState(): Bundle = when (val state = state) {

        Uninitialized -> error("saveState() cannot be called with the state set to \"Uninitialized\" because each time \"registry.register()\" is called, the state is updated to \"Ready\" right after")

        is Ready -> bundleOf(
            TYPE_KEY to typeForValue(state.value).ordinal,
            VALUE_KEY to state.value
        )
    }

    private val registry = object {
        private fun key(property: KProperty<*>) = "StateSaver_" + property.name

        fun register(registry: SavedStateRegistryOwner, property: KProperty<*>) {
            val keyName = key(property)
            if (registry.savedStateRegistry.getSavedStateProvider(keyName) !== this@StateSaver)
                registry.savedStateRegistry.registerSavedStateProvider(keyName, this@StateSaver)
        }

        fun consume(registry: SavedStateRegistryOwner, property: KProperty<*>): Bundle? = registry
            .savedStateRegistry
            .consumeRestoredStateForKey(key(property))
    }

    // This code was inspired by https://gist.github.com/fathonyfath/6b791677f3732c13f456e1661796b9a5
    enum class SupportedTypes {
        Null,
        Boolean, Byte, Char, Double, Float, Int, Long, Short,
        Bundle, CharSequence, Parcelable,
        BooleanArray, ByteArray, CharArray, DoubleArray, FloatArray, IntArray, LongArray, ShortArray,
        ParcelableArray, StringArray, CharSequenceArray,
        Serializable,
        IBinder, Size, SizeF;

        @Suppress("DEPRECATION")
        @SuppressLint("ObsoleteSdkInt")
        companion object {
            fun typeForValue(value: Any?): SupportedTypes = when (value) {
                null -> Null
                is kotlin.Boolean -> Boolean
                is kotlin.Byte -> Byte
                is kotlin.Char -> Char
                is kotlin.Double -> Double
                is kotlin.Float -> Float
                is kotlin.Int -> Int
                is kotlin.Long -> Long
                is kotlin.Short -> Short

                is android.os.Bundle -> Bundle
                is kotlin.CharSequence -> CharSequence
                is android.os.Parcelable -> Parcelable

                is kotlin.BooleanArray -> BooleanArray
                is kotlin.ByteArray -> ByteArray
                is kotlin.CharArray -> CharArray
                is kotlin.DoubleArray -> DoubleArray
                is kotlin.FloatArray -> FloatArray
                is kotlin.IntArray -> IntArray
                is kotlin.LongArray -> LongArray
                is kotlin.ShortArray -> ShortArray

                is Array<*> -> {
                    val componentType = value::class.java.componentType!!
                    when {
                        android.os.Parcelable::class.java.isAssignableFrom(componentType) -> ParcelableArray

                        String::class.java.isAssignableFrom(componentType) -> StringArray

                        kotlin.CharSequence::class.java.isAssignableFrom(componentType) -> CharSequenceArray

                        java.io.Serializable::class.java.isAssignableFrom(componentType) -> Serializable

                        else -> {
                            val valueType = componentType.canonicalName
                            throw IllegalArgumentException("Illegal value array type $valueType")
                        }
                    }
                }

                is java.io.Serializable -> Serializable

                else -> when {
                    SDK_INT >= 18 && value is android.os.IBinder -> IBinder
                    SDK_INT >= 21 && value is android.util.Size -> Size
                    SDK_INT >= 21 && value is android.util.SizeF -> SizeF
                    else -> {
                        val valueType = value.javaClass.canonicalName
                        throw IllegalArgumentException("Illegal value type $valueType")
                    }
                }
            }

            fun android.os.Bundle.get(type: SupportedTypes, key: String): Any? = when (type) {
                Null -> getString(key)

                Boolean -> getBoolean(key)
                Byte -> getByte(key)
                Char -> getChar(key)
                Double -> getDouble(key)
                Float -> getFloat(key)
                Int -> getInt(key)
                Long -> getLong(key)
                Short -> getShort(key)

                Bundle -> getBundle(key)
                CharSequence -> getCharSequence(key)
                Parcelable -> getParcelable(key)

                BooleanArray -> getBooleanArray(key)
                ByteArray -> getByteArray(key)
                CharArray -> getCharArray(key)
                DoubleArray -> getDoubleArray(key)
                FloatArray -> getFloatArray(key)
                IntArray -> getIntArray(key)
                LongArray -> getLongArray(key)
                ShortArray -> getShortArray(key)

                ParcelableArray -> getParcelableArray(key)
                StringArray -> getStringArray(key)
                CharSequenceArray -> getCharSequenceArray(key)

                Serializable -> getSerializable(key)

                IBinder -> if (SDK_INT >= 18) getBinder(key) else null
                Size -> if (SDK_INT >= 21) getSize(key) else null
                SizeF -> if (SDK_INT >= 21) getSizeF(key) else null
            }
        }
    }

    companion object {
        private const val TYPE_KEY = "TYPE_KEY"
        private const val VALUE_KEY = "VALUE_KEY"
    }
}
