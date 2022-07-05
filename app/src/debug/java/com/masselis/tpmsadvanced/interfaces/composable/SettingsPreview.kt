package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.mocks

@Preview
@Composable
fun SettingsPreview() {
    TpmsAdvancedTheme {
        SettingsViewModel.mocks().forEach { mock ->
            Settings(
                viewModel = mock,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}