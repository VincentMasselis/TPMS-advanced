package com.masselis.tpmsadvanced.mock

import com.masselis.tpmsadvanced.interfaces.viewmodel.SensorFavouriteViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito
import org.mockito.Mockito.mock

private fun mock(state: SensorFavouriteViewModel.State) = mock(SensorFavouriteViewModel::class.java).also {
    Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
}

fun SensorFavouriteViewModel.Companion.mocks() = listOf(
    mock(SensorFavouriteViewModel.State.Empty),
    mock(SensorFavouriteViewModel.State.RequestBond(420))
)