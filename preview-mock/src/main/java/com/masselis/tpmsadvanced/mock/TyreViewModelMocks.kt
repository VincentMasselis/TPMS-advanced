package com.masselis.tpmsadvanced.mock

import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.model.Fraction
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

private fun mock(state: TyreViewModel.State) = Mockito.mock(TyreViewModel::class.java).also {
    Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
}

val TyreViewModel.Companion.mocks
    get() = listOf(
        mock(TyreViewModel.State.NotDetected),
        mock(TyreViewModel.State.Obsolete),
        mock(TyreViewModel.State.Normal.BlueToGreen(Fraction(0.5f))),
        mock(TyreViewModel.State.Normal.GreenToRed(Fraction(0f))),
        mock(TyreViewModel.State.Normal.GreenToRed(Fraction(0.5f))),
        mock(TyreViewModel.State.Alerting)
    )