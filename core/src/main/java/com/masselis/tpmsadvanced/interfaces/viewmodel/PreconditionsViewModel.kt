package com.masselis.tpmsadvanced.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.BleScanUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalCoroutinesApi::class)
class PreconditionsViewModel @AssistedInject constructor(
    bleScanUseCase: BleScanUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): PreconditionsViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Loading : State()

        @Parcelize
        object Ready : State()

        @Parcelize
        data class MissingPermission(val permission: String) : State()

        @Parcelize
        object BluetoothChipTurnedOff : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Loading)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()
    private val trigger = MutableSharedFlow<Unit>(1).also { it.tryEmit(Unit) }

    init {
        trigger
            .flatMapLatest {
                combine(
                    bleScanUseCase.isChipTurnedOn(),
                    flowOf(bleScanUseCase.missingPermission())
                ) { isOn, permission ->
                    when {
                        isOn.not() -> State.BluetoothChipTurnedOff
                        permission != null -> State.MissingPermission(permission)
                        else -> State.Ready
                    }
                }
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun trigger() {
        viewModelScope.launch { trigger.emit(Unit) }
    }

    companion object
}