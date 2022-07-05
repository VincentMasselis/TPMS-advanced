package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.mocks
import com.masselis.tpmsadvanced.model.TyreLocation

@Preview
@Composable
fun TyreStatPreview() {
    LazyColumn {
        items(TyreStatsViewModel.mocks) {
            TyreStat(
                location = TyreLocation.FRONT_LEFT,
                modifier = Modifier.width(350.dp),
                viewModel = it
            )
        }
    }
}