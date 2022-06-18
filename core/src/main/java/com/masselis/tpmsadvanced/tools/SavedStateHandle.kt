package com.masselis.tpmsadvanced.tools

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow

fun <T : Any> MutableLiveData<T>.asMutableStateFlow(): MutableStateFlow<T> = LiveDataFlow(this)

private class LiveDataFlow<T : Any> private constructor(
    private val liveData: MutableLiveData<T>,
    private val stateFlow: MutableStateFlow<T>
) : MutableStateFlow<T> by stateFlow {
    constructor(liveData: MutableLiveData<T>) : this(liveData, MutableStateFlow(liveData.value!!))

    override var value: T
        get() = liveData.value!!
        set(value) {
            liveData.value = value
            stateFlow.value = value
        }

    override fun compareAndSet(expect: T, update: T): Boolean {
        liveData.value = update
        return stateFlow.compareAndSet(expect, update)
    }

    override suspend fun emit(value: T) {
        liveData.value = value
        stateFlow.emit(value)
    }

    override fun tryEmit(value: T): Boolean {
        liveData.value = value
        return stateFlow.tryEmit(value)
    }
}