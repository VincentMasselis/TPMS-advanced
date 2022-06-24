@file:Suppress("NAME_SHADOWING")

package com.masselis.tpmsadvanced.interfaces.composable

import android.animation.ArgbEvaluator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State.Normal.BlueToGreen
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State.Normal.GreenToRed
import com.masselis.tpmsadvanced.interfaces.viewmodel.utils.savedStateViewModel
import com.masselis.tpmsadvanced.mock.mocks
import com.masselis.tpmsadvanced.model.TyreLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val evaluator = ArgbEvaluator()

@Composable
fun Tyre(
    location: TyreLocation,
    modifier: Modifier = Modifier,
    viewModel: TyreViewModel = savedStateViewModel(key = "TyreViewModel_${location.name}") {
        mainComponent.findTyreComponent.find(location).tyreViewModelFactory.build(it)
    },
) {
    val state by viewModel.stateFlow.collectAsState()
    val isVisible = remember { mutableStateOf(true) }
    if (state is TyreViewModel.State.Alerting) {
        LaunchedEffect(key1 = isVisible) {
            launch {
                repeat(Int.MAX_VALUE) {
                    delay(300.milliseconds)
                    isVisible.value = !isVisible.value
                }
            }
        }
    }
    Box(
        modifier
            .alpha(if (isVisible.value) 1f else 0f)
            .clip(RoundedCornerShape(percent = 20))
            .aspectRatio(15f / 40f)
            .run {
                when (val state = state) {
                    TyreViewModel.State.NotDetected -> this
                        .background(Color.Transparent)
                        .border(2.dp, Color.Gray, RoundedCornerShape(percent = 20))
                    is TyreViewModel.State.Normal ->
                        background(
                            Color(
                                evaluator.evaluate(
                                    state.fraction.value,
                                    when (state) {
                                        is BlueToGreen -> Color.Blue.toArgb()
                                        is GreenToRed -> Color.Green.toArgb()
                                    },
                                    when (state) {
                                        is BlueToGreen -> Color.Green.toArgb()
                                        is GreenToRed -> Color.Red.toArgb()
                                    },
                                ) as Int
                            )
                        )
                    is TyreViewModel.State.Obsolete ->
                        background(Color.Gray)
                    is TyreViewModel.State.Alerting ->
                        background(Color.Red)
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun TyrePreview() {
    LazyColumn {
        items(TyreViewModel.mocks) { viewModel ->
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