package com.masselis.tpmsadvanced.mock

import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.model.Fraction
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

object TyreViewModel {

    private fun mock(state: TyreViewModel.State) = Mockito.mock(TyreViewModel::class.java).also {
        Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
    }

    val notDetected = mock(TyreViewModel.State.NotDetected)
    val obsolete = mock(TyreViewModel.State.Obsolete)
    val normalGreenToRead = mock(TyreViewModel.State.Normal.GreenToRed(Fraction(0.5f)))
    val normalBlueToGreen = mock(TyreViewModel.State.Normal.BlueToGreen(Fraction(0.5f)))
    val alerting = mock(TyreViewModel.State.Alerting)
}