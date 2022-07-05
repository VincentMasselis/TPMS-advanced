package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.TyreLocation
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

@Preview
@Composable
fun TyreStatPreview() {
    LazyColumn {
        items(
            listOf(
                mock(TyreStatsViewModel.State.NotDetected),
                mock(
                    TyreStatsViewModel.State.Normal(
                        Pressure(200.978f),
                        Pressure.Unit.BAR,
                        Temperature(25.78f),
                        Temperature.Unit.CELSIUS
                    )
                ),
                mock(
                    TyreStatsViewModel.State.Alerting(
                        Pressure(0f),
                        Pressure.Unit.BAR,
                        Temperature(25f),
                        Temperature.Unit.CELSIUS
                    )
                )
            )
        ) {
            TyreStat(
                location = TyreLocation.FRONT_LEFT,
                modifier = Modifier.width(350.dp),
                viewModel = it
            )
        }
    }
}

private fun mock(state: TyreStatsViewModel.State) = Mockito
    .mock(TyreStatsViewModel::class.java).also {
        Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
    }