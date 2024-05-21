package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import javax.inject.Inject


internal class PreconditionsViewModel @Inject constructor(
    private val bluetoothLeScanner: BluetoothLeScanner,
) : ViewModel() {

    fun requiredPermission() = bluetoothLeScanner.missingPermission()

    fun isBluetoothRequired() = bluetoothLeScanner.isBluetoothRequired
}
