package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.interfaces.composable.Tyre
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.TyreViewModel.State.*
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.model.Fraction
import com.masselis.tpmsadvanced.core.model.TyreLocation
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@Preview(showBackground = true)
@Composable
fun TyrePreview() {
    TpmsAdvancedTheme {
        LazyColumn {
            items(
                listOf(
                    mock(NotDetected),
                    mock(Normal.BlueToGreen(Fraction(0.5f))),
                    mock(Normal.GreenToRed(Fraction(0f))),
                    mock(Normal.GreenToRed(Fraction(0.5f))),
                    mock(Alerting)
                )
            ) { viewModel ->
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

private fun mock(state: State) = mock<TyreViewModelImpl> {
    on(it.stateFlow) doReturn MutableStateFlow(state)
}