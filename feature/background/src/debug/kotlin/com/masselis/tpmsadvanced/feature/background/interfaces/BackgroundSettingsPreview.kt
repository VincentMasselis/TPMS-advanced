package com.masselis.tpmsadvanced.feature.background.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.AutomaticBackgroundViewModel.State
import com.masselis.tpmsadvanced.feature.background.interfaces.ui.AutomaticBackgroundSettings
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

@Preview
@Composable
internal fun BackgroundSettingsPreview() {
    AutomaticBackgroundSettings(
        mockk {
            every { vehicle } returns
                    mockk {
                        every { uuid } returns UUID.randomUUID()
                        every { name } returns "MOCK"
                    }
        },
        viewModel = mockk(relaxed = true) {
            every { stateFlow } returns MutableStateFlow(State.MonitorDisabled)
        }
    )
}
