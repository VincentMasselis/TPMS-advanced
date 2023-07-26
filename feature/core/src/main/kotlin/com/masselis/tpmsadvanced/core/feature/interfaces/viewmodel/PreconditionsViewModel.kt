package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import javax.inject.Inject


internal class PreconditionsViewModel @Inject constructor(
    private val bluetoothLeScanner: BluetoothLeScanner,
) : ViewModel() {

    fun requiredPermission() = bluetoothLeScanner.missingPermission()

    fun isBluetoothRequired() = bluetoothLeScanner.isBluetoothRequired
}
