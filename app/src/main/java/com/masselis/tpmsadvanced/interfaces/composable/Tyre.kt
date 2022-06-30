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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.viewmodel.RealTyreViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State
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
        location.component.realTyreViewModelFactory.build(it)
    },
) {
    val state by viewModel.stateFlow.collectAsState()
    val isVisible = remember { mutableStateOf(true) }
    if (state is State.Alerting) {
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
                    is State.Obsolete ->
                        background(MaterialTheme.colorScheme.onBackground)
                    is State.Alerting ->
                        background(MaterialTheme.colorScheme.error)
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun TyrePreview() {
    TpmsAdvancedTheme {
        LazyColumn {
            items(RealTyreViewModel.mocks) { viewModel ->
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