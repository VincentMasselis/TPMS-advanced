package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.Temperature.CREATOR.celsius
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

@Preview
@Composable
fun SettingsPreview() {
    TpmsAdvancedTheme {
        listOf(
            mock(
                MutableStateFlow(1f.bar),
                MutableStateFlow(20f.celsius),
                MutableStateFlow(45f.celsius),
                MutableStateFlow(90f.celsius)
            )
        ).forEach { mock ->
            Settings(
                viewModel = mock,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

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