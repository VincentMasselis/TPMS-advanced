@file:Suppress("NAME_SHADOWING")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import android.animation.ArgbEvaluator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val evaluator = ArgbEvaluator()

@Composable
internal fun Tyre(
    location: SensorLocation,
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: TyreViewModel = viewModel(key = "TyreViewModel_${vehicleComponent.hashCode()}_${location.name}") {
        vehicleComponent.tyreComponent(location).tyreViewModelFactory.build(createSavedStateHandle())
    },
) {
    val state by viewModel.stateFlow.collectAsState()
    var isVisible by remember { mutableStateOf(true) }
    if (state is State.Alerting) {
        LaunchedEffect(key1 = isVisible) {
            launch {
                repeat(Int.MAX_VALUE) {
                    delay(300.milliseconds)
                    isVisible = !isVisible
                }
            }
        }
    } else
        isVisible = true
    Box(
        modifier
            .alpha(if (isVisible) 1f else 0f)
            .clip(RoundedCornerShape(percent = 20))
            .aspectRatio(15f / 40f)
            .run {
                when (val state = state) {
                    State.NotDetected -> this
                        .background(Color.Transparent)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.onBackground,
                            RoundedCornerShape(percent = 20)
                        )
                    is State.Normal ->
                        background(
                            Color(
                                evaluator.evaluate(
                                    state.fraction.value,
                                    when (state) {
                                        is State.Normal.BlueToGreen -> MaterialTheme.colorScheme.tertiary.toArgb()
                                        is State.Normal.GreenToRed -> MaterialTheme.colorScheme.primary.toArgb()
                                    },
                                    when (state) {
                                        is State.Normal.BlueToGreen -> MaterialTheme.colorScheme.primary.toArgb()
                                        is State.Normal.GreenToRed -> MaterialTheme.colorScheme.error.toArgb()
                                    },
                                ) as Int
                            )
                        )
                    is State.Alerting ->
                        background(MaterialTheme.colorScheme.error)
                }
            }
    )
}
