package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.interfaces.viewmodel.mocks
import com.masselis.tpmsadvanced.model.TyreLocation


@Preview(showBackground = true)
@Composable
fun TyrePreview() {
    TpmsAdvancedTheme {
        LazyColumn {
            items(TyreViewModelImpl.mocks) { viewModel ->
                Tyre(
                    location = TyreLocation.FRONT_RIGHT,
                    modifier = Modifier
                        .height(150.dp)
                        .width(40.dp),
                    viewModel = viewModel
                )
            }
        }
    }
}