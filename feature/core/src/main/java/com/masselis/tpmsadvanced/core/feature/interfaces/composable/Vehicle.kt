@file:Suppress("NAME_SHADOWING")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleComponentViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.ui.KeepScreenOn
import com.masselis.tpmsadvanced.data.record.model.SensorLocation

@Composable
public fun Vehicle(modifier: Modifier = Modifier) {
    Vehicle(
        modifier,
        viewModel { FeatureCoreComponent.currentVehicleComponentViewModel }
    )
}

@Suppress("LongMethod")
@Composable
internal fun Vehicle(
    modifier: Modifier = Modifier,
    viewModel: CurrentVehicleComponentViewModel = viewModel {
        FeatureCoreComponent.currentVehicleComponentViewModel
    }
) {
    KeepScreenOn()
    val component by viewModel.stateFlow.collectAsState()
    CompositionLocalProvider(LocalVehicleComponent provides component) {
        ConstraintLayout(modifier = modifier) {
            val (carConst,
                tyreBox,
                topLeftStats,
                topLeftFav,
                topRightStats,
                topRightFav,
                bottomLeftStats,
                bottomLeftFav,
                bottomRightStats,
                bottomRightFav
            ) = createRefs()
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.schema_car_top_view),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(208f / 462f)
                    .constrainAs(carConst) {
                        centerTo(parent)
                        height = Dimension.percent(.7f)
                    }
            )
            Box(
                Modifier
                    .aspectRatio(235f / 462f)
                    .constrainAs(tyreBox) {
                        centerTo(parent)
                        height = Dimension.percent(.55f)
                    }
            ) {
                Tyre(
                    location = SensorLocation.FRONT_LEFT,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxHeight(.2f)
                        .fillMaxWidth(.1f)
                )
                Tyre(
                    location = SensorLocation.FRONT_RIGHT,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxHeight(.2f)
                        .fillMaxWidth(.1f)
                )
                Tyre(
                    location = SensorLocation.REAR_LEFT,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxHeight(.2f)
                        .fillMaxWidth(.12f)
                )
                Tyre(
                    location = SensorLocation.REAR_RIGHT,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxHeight(.2f)
                        .fillMaxWidth(.12f)
                )
            }
            Box(Modifier
                .constrainAs(topLeftStats) {
                    top.linkTo(tyreBox.top)
                    end.linkTo(tyreBox.start, 8.dp)
                }
                .width(100.dp)
            ) {
                TyreStat(
                    location = SensorLocation.FRONT_LEFT,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
            Box(Modifier.constrainAs(topLeftFav) {
                top.linkTo(tyreBox.top)
                start.linkTo(tyreBox.start)
            }) {
                BindSensorButton(
                    SensorLocation.FRONT_LEFT,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            Box(Modifier
                .constrainAs(topRightStats) {
                    top.linkTo(tyreBox.top)
                    start.linkTo(tyreBox.end, 8.dp)
                }
                .width(100.dp)
            ) {
                TyreStat(
                    location = SensorLocation.FRONT_RIGHT,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }
            Box(Modifier.constrainAs(topRightFav) {
                top.linkTo(tyreBox.top)
                end.linkTo(tyreBox.end)
            }) {
                BindSensorButton(
                    SensorLocation.FRONT_RIGHT,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
            Box(Modifier
                .constrainAs(bottomLeftStats) {
                    bottom.linkTo(tyreBox.bottom)
                    end.linkTo(tyreBox.start, 8.dp)
                }
                .width(100.dp)
            ) {
                TyreStat(
                    location = SensorLocation.REAR_LEFT,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
            Box(Modifier.constrainAs(bottomLeftFav) {
                bottom.linkTo(tyreBox.bottom)
                start.linkTo(tyreBox.start)
            }) {
                BindSensorButton(
                    SensorLocation.REAR_LEFT,
                    modifier = Modifier.padding(start = 28.dp)
                )
            }
            Box(Modifier
                .constrainAs(bottomRightStats) {
                    bottom.linkTo(tyreBox.bottom)
                    start.linkTo(tyreBox.end, 8.dp)
                }
                .width(100.dp)
            ) {
                TyreStat(
                    location = SensorLocation.REAR_RIGHT,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }
            Box(Modifier.constrainAs(bottomRightFav) {
                bottom.linkTo(tyreBox.bottom)
                end.linkTo(tyreBox.end)
            }) {
                BindSensorButton(
                    SensorLocation.REAR_RIGHT,
                    modifier = Modifier.padding(end = 28.dp)
                )
            }
        }
    }
}
