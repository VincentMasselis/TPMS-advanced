package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.model.TyreLocation
import io.mockk.mockk

@Preview
@Composable
fun TyrePreview() {
    Tyre(
        location = TyreLocation.REAR_RIGHT,
        viewModel = mockk()
    )
}