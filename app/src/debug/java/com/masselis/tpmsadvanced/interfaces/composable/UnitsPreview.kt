package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.unit.interfaces.Units
import com.masselis.tpmsadvanced.unit.interfaces.UnitsViewModel
import com.masselis.tpmsadvanced.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.unit.model.TemperatureUnit.CELSIUS
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

@Preview
@Composable
fun UnitsPreview() {
    TpmsAdvancedTheme {
        Units(
            viewModel = Mockito.mock(UnitsViewModel::class.java).also {
                Mockito.`when`(it.pressure).thenReturn(MutableStateFlow(BAR))
                Mockito.`when`(it.temperature).thenReturn(MutableStateFlow(CELSIUS))
            }
        )
    }
}