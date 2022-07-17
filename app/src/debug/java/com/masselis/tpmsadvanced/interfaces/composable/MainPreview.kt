package com.masselis.tpmsadvanced.interfaces.composable

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.PreconditionsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    LazyColumn {
        items(
            listOf<PreconditionsViewModel>(
                mock(PreconditionsViewModel.State.Ready),
                mock(PreconditionsViewModel.State.BluetoothChipTurnedOff),
                mock(PreconditionsViewModel.State.Loading),
                mock(
                    PreconditionsViewModel.State.MissingPermission(
                        listOf(
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN
                        )
                    )
                )
            )
        ) {
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

private fun mock(state: PreconditionsViewModel.State) =
    Mockito.mock(PreconditionsViewModel::class.java).also {
        Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
    }