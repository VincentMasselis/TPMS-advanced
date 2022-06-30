package com.masselis.tpmsadvanced.mock

import com.masselis.tpmsadvanced.interfaces.viewmodel.RealTyreViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.model.Fraction
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

private fun mock(state: State) = Mockito.mock(RealTyreViewModel::class.java).also {
    Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
}

val RealTyreViewModel.Companion.mocks
    get() = listOf(
        mock(State.NotDetected),
        mock(State.Obsolete),
        mock(State.Normal.BlueToGreen(Fraction(0.5f))),
        mock(State.Normal.GreenToRed(Fraction(0f))),
        mock(State.Normal.GreenToRed(Fraction(0.5f))),
        mock(State.Alerting)
    )