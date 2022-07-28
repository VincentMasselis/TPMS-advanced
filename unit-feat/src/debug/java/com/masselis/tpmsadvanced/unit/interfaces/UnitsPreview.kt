package com.masselis.tpmsadvanced.unit.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.unit.model.TemperatureUnit.CELSIUS
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@Preview
@Composable
fun UnitsPreview() {
    Units(
        viewModel = mock {
            on(mock.pressure) doReturn MutableStateFlow(BAR)
            on(mock.temperature) doReturn MutableStateFlow(CELSIUS)
        }
    )
}