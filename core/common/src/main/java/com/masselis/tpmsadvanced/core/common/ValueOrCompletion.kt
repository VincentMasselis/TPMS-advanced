package com.masselis.tpmsadvanced.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transformWhile

// Copy/Pasted from https://github.com/Kotlin/kotlinx.coroutines/issues/2092#issuecomment-1433092654

public sealed class ValueOrCompletion<out T> {
    public data class Value<out T>(val value: T) : ValueOrCompletion<T>()
    public data class Completion(val exception: Throwable?) : ValueOrCompletion<Nothing>()
}

public fun <T> Flow<T>.materializeCompletion(): Flow<ValueOrCompletion<T>> = flow {
    val result = runCatching {
        collect { emit(ValueOrCompletion.Value(it)) }
    }
    emit(ValueOrCompletion.Completion(result.exceptionOrNull()))
}

public fun <T> Flow<ValueOrCompletion<T>>.dematerializeCompletion(): Flow<T> =
    transformWhile { vc ->
        when (vc) {
            is ValueOrCompletion.Value -> {
                emit(vc.value)
                true
            }

            is ValueOrCompletion.Completion -> {
                vc.exception?.let { throw it }
                false
            }
        }
    }
