package com.masselis.tpmsadvanced.unit.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.Units
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow

@Preview
@Composable
internal fun UnitsPreview() {
    Units(
        viewModel = mockk {
            every { pressure } returns MutableStateFlow(BAR)
            every { temperature } returns MutableStateFlow(CELSIUS)
        }
    )
}