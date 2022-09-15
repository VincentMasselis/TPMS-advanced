package com.masselis.tpmsadvanced.unit.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.Units
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@Preview
@Composable
internal fun UnitsPreview() {
    Units(
        viewModel = mock {
            on(mock.pressure) doReturn MutableStateFlow(BAR)
            on(mock.temperature) doReturn MutableStateFlow(CELSIUS)
        }
    )
}