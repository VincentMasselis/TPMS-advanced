package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun CurrentVehicleDropdownPreview() {
    CurrentVehicleDropdown(
        viewModel = previewCurrentVehicleDropdownViewModel()
    )
}
