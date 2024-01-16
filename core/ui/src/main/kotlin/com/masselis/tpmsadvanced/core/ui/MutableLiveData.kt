package com.masselis.tpmsadvanced.core.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

context(CoroutineScope)
public fun <T> MutableLiveData<T>.asMutableStateFlow(): MutableStateFlow<T> =
    MutableStateFlow(this@CoroutineScope, this)

context(ViewModel)
public fun <T> MutableLiveData<T>.asMutableStateFlow(): MutableStateFlow<T> =
    MutableStateFlow(viewModelScope, this)

@Suppress("UNCHECKED_CAST", "FunctionNaming")
private fun <T> MutableStateFlow(
    scope: CoroutineScope,
    liveData: MutableLiveData<T>
): MutableStateFlow<T> =
    MutableStateFlow(
        require(liveData.isInitialized) { "Cannot use this MutableStateFlow if the source LiveData is not initialized" }
            .let { liveData.value as T }
    ).also { stateFlow ->
        // The following code creates a 2-way binding between the MutableLiveData and the MutableStateFlow

        // From the MutableLiveData to the MutableStateFlow
        callbackFlow<T> {
            val observer = Observer<T> {
                stateFlow.value = it
            }
            liveData.observeForever(observer)
            awaitClose { liveData.removeObserver(observer) }
        }.launchIn(scope)

        // From the MutableStateFlow to the MutableLiveData
        stateFlow
            .onEach {
                if (liveData.value != it)
                    liveData.value = it
            }
            .flowOn(Dispatchers.Main.immediate)
            .launchIn(scope)
    }

context(CoroutineScope)
public fun <T> SavedStateHandle.getMutableStateFlow(
    key: String,
    initialValue: () -> T = missingInitialValue(key)
): MutableStateFlow<T> = this
    .getLiveData<T>(key, initialValue)
    .asMutableStateFlow()

context(ViewModel)
public fun <T> SavedStateHandle.getMutableStateFlow(
    key: String,
    initialValue: () -> T = missingInitialValue(key)
): MutableStateFlow<T> = this
    .getLiveData<T>(key, initialValue)
    .asMutableStateFlow()

private fun <T> missingInitialValue(key: String): () -> T = {
    throw IllegalArgumentException("Missing initial value for this key: $key")
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private fun <T> SavedStateHandle.getLiveData(
    key: String,
    initialValue: () -> T
) = synchronized(this) {
    if (contains(key).not())
        getLiveData(key, initialValue())
    else
        getLiveData(key)
}
