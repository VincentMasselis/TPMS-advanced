package com.masselis.tpmsadvanced.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.QrCodeAnalyserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class CameraPreconditionsViewModel @AssistedInject constructor(
    private val qrCodeAnalyserUseCase: QrCodeAnalyserUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): CameraPreconditionsViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Loading : State()

        @Parcelize
        object Ready : State()

        @Parcelize
        data class MissingPermission(val permission: String) : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Loading)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow
    private val trigger = MutableSharedFlow<Unit>(1).also { it.tryEmit(Unit) }

    init {
        trigger
            .map {
                qrCodeAnalyserUseCase.missingPermission()
                    ?.let { State.MissingPermission(it) }
                    ?: State.Ready
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun trigger() {
        viewModelScope.launch { trigger.emit(Unit) }
    }
}