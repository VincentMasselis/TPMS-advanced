package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.mocks


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    LazyColumn {
        items(PreconditionsViewModel.mocks()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Main(viewModel = it)
            }
        }
    }
}