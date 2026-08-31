package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DemoModeSwitchViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DemoModeSwitchViewModel.State
import com.masselis.tpmsadvanced.feature.main.ioc.Bindings.Companion.DemoModeSwitchViewModel

@Composable
internal fun DemoModeSwitch(
    modifier: Modifier = Modifier,
    viewModel: DemoModeSwitchViewModel = viewModel { DemoModeSwitchViewModel() }
) {
    val state by viewModel.stateFlow.collectAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = when (state) {
                    State.Disabled -> "Enable demo mode"
                    State.Enabled -> "Demo mode is enabled"
                },
                textAlign = TextAlign.End,
            )
            Text(
                text = "Triggers an application restart",
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = when (state) {
                State.Disabled -> false
                State.Enabled -> true
            },
            onCheckedChange = { isEnabled ->
                if (isEnabled) viewModel.enable()
                else viewModel.disable()
            },
        )
    }
}
