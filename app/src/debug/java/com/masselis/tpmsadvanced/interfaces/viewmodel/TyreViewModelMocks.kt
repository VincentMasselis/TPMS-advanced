package com.masselis.tpmsadvanced.interfaces.viewmodel

import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.model.Fraction
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

private fun mock(state: State) = Mockito.mock(TyreViewModelImpl::class.java).also {
    Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
}

val TyreViewModelImpl.Companion.mocks
    get() = listOf(
        mock(State.NotDetected),
        mock(State.Normal.BlueToGreen(Fraction(0.5f))),
        mock(State.Normal.GreenToRed(Fraction(0f))),
        mock(State.Normal.GreenToRed(Fraction(0.5f))),
        mock(State.Alerting)
    )