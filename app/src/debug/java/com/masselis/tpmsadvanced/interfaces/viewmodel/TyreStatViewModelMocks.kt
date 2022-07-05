package com.masselis.tpmsadvanced.interfaces.viewmodel

import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

private fun mock(state: TyreStatsViewModel.State) =
    Mockito.mock(TyreStatsViewModel::class.java).also {
        Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
    }

val TyreStatsViewModel.Companion.mocks
    get() = listOf(
        mock(TyreStatsViewModel.State.NotDetected),
        mock(TyreStatsViewModel.State.Normal(Pressure(200.978f), Temperature(25.78f))),
        mock(TyreStatsViewModel.State.Alerting(Pressure(0f), Temperature(25f)))
    )