package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner


internal class PreconditionsViewModel(
    private val bluetoothLeScanner: BluetoothLeScanner,
) : ViewModel() {

    fun requiredPermission() = bluetoothLeScanner.missingPermission()

    fun isBluetoothRequired() = bluetoothLeScanner.isBluetoothRequired
}
