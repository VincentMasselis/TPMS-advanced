package com.masselis.tpmsadvanced.mock

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import com.masselis.tpmsadvanced.interfaces.viewmodel.PreconditionsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito
import org.mockito.Mockito.mock

private fun mock(state: PreconditionsViewModel.State) =
    mock(PreconditionsViewModel::class.java).also {
        Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
    }

@SuppressLint("InlinedApi")
fun PreconditionsViewModel.Companion.mocks() = listOf<PreconditionsViewModel>(
    mock(PreconditionsViewModel.State.Ready),
    mock(PreconditionsViewModel.State.BluetoothChipTurnedOff),
    mock(PreconditionsViewModel.State.Loading),
    mock(PreconditionsViewModel.State.MissingPermission(listOf(BLUETOOTH_CONNECT, BLUETOOTH_SCAN)))
)