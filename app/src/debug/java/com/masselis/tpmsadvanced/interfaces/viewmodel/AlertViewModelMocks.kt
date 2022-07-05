package com.masselis.tpmsadvanced.interfaces.viewmodel

import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.Temperature.CREATOR.celsius
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

private fun mock(
    lowPressure: MutableStateFlow<Pressure>,
    lowTemp: MutableStateFlow<Temperature>,
    normalTemp: MutableStateFlow<Temperature>,
    highTemp: MutableStateFlow<Temperature>
) = Mockito.mock(SettingsViewModel::class.java).also {
    Mockito.`when`(it.lowPressure).thenReturn(lowPressure)
    Mockito.`when`(it.lowTemp).thenReturn(lowTemp)
    Mockito.`when`(it.normalTemp).thenReturn(normalTemp)
    Mockito.`when`(it.highTemp).thenReturn(highTemp)
}

fun SettingsViewModel.Companion.mocks() = listOf(
    mock(
        MutableStateFlow(1f.bar),
        MutableStateFlow(20f.celsius),
        MutableStateFlow(45f.celsius),
        MutableStateFlow(90f.celsius)
    )
)