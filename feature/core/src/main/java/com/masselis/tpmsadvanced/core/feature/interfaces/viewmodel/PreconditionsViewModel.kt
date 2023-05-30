package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class PreconditionsViewModel @AssistedInject constructor(
    private val bluetoothLeScanner: BluetoothLeScanner,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): PreconditionsViewModel
    }

    fun requiredPermission() = bluetoothLeScanner.missingPermission()
}
