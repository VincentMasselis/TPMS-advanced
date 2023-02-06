package com.masselis.tpmsadvanced.unit.interfaces

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.tooling.LocalInspectionTables
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.Units
import com.masselis.tpmsadvanced.core.ui.findActivity
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow

@Preview
@Composable
internal fun UnitsPreview() {
    Column {
        Text(text = "${LocalContext.current.findActivity()}")
        Text(text = "${LocalInspectionMode.current}")
        Text(text = "${LocalInspectionTables.current}")
    }
    /*Units(
        viewModel = mockk {
            every { pressure } returns MutableStateFlow(BAR)
            every { temperature } returns MutableStateFlow(CELSIUS)
        }
    )*/
}