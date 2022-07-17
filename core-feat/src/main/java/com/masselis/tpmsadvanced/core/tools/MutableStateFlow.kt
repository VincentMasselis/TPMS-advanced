package com.masselis.tpmsadvanced.core.tools

import kotlinx.coroutines.flow.MutableStateFlow

class ObservableStateFlow<T> private constructor(
    private val stateFlow: MutableStateFlow<T>,
    private val onValue: (oldValue: T, newValue: T) -> Unit
) : MutableStateFlow<T> by stateFlow {

    constructor(initialValue: T, onValue: (oldValue: T, newValue: T) -> Unit) :
            this(MutableStateFlow(initialValue), onValue)

    override var value: T
        get() = stateFlow.value
        set(value) {
            val previous = stateFlow.value
            stateFlow.value = value
            onValue(previous, value)
        }

    override fun compareAndSet(expect: T, update: T): Boolean {
        val previous = stateFlow.value
        return stateFlow.compareAndSet(expect, update).also { wasUpdated ->
            if (wasUpdated)
                onValue(previous, update)
        }
    }

    override suspend fun emit(value: T) {
        stateFlow.emit(value)
        onValue(stateFlow.value, value)
    }

    override fun tryEmit(value: T): Boolean = stateFlow.tryEmit(value)
        .also { onValue(stateFlow.value, value) }
}