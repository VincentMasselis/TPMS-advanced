@file:Suppress("NAME_SHADOWING")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleComponentViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.model.ManySensor
import com.masselis.tpmsadvanced.core.ui.KeepScreenOn
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.RIGHT

@Composable
public fun Vehicle(modifier: Modifier = Modifier) {
    Vehicle(
        modifier,
        viewModel {
            FeatureCoreComponent.currentVehicleComponentViewModel.build(createSavedStateHandle())
        }
    )
}

@Suppress("LongMethod")
@Composable
internal fun Vehicle(
    modifier: Modifier = Modifier,
    viewModel: CurrentVehicleComponentViewModel = viewModel {
        FeatureCoreComponent.currentVehicleComponentViewModel.build(createSavedStateHandle())
    }
) {
    KeepScreenOn()
    val component by viewModel.stateFlow.collectAsState()
    CompositionLocalProvider(LocalVehicleComponent provides component) {
        when (component.vehicle.kind) {
            Kind.CAR -> Car(modifier)
            Kind.SINGLE_AXLE_TRAILER -> SingleAxleTrailer(modifier)
            Kind.MOTORCYCLE -> Motorcycle(modifier)
            Kind.TADPOLE_THREE_WHEELER -> TadpoleThreadWheeler(modifier)
            Kind.DELTA_THREE_WHEELER -> DeltaThreeWheeler(modifier)
        }
    }
}

@Composable
private fun Car(
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (carConst,
            tyreBox,
            frontLeft,
            frontLeftStats,
            frontLeftBinding,
            frontRight,
            frontRightStats,
            frontRightBinding,
            rearLeft,
            rearLeftStats,
            rearLeftBinding,
            rearRight,
            rearRightStats,
            rearRightBinding
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
        )
        with(ManySensor.Located(FRONT_LEFT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(frontLeft) {
                    top.linkTo(tyreBox.top)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(frontLeftStats) {
                    top.linkTo(frontLeft.top)
                    end.linkTo(frontLeft.start, 8.dp)
                    // If not, the word "bar" for "1,50 bar" is not displayed 🤷
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(frontLeftBinding) {
                    top.linkTo(frontLeft.top)
                    start.linkTo(frontLeft.end)
                }
            )
        }
        with(ManySensor.Located(FRONT_RIGHT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(frontRight) {
                    top.linkTo(tyreBox.top)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(frontRightStats) {
                    top.linkTo(frontRight.top)
                    start.linkTo(frontRight.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(frontRightBinding) {
                    top.linkTo(frontRight.top)
                    end.linkTo(frontRight.start)
                }
            )
        }
        with(ManySensor.Located(REAR_LEFT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(rearLeft) {
                    bottom.linkTo(tyreBox.bottom)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(rearLeftStats) {
                    bottom.linkTo(rearLeft.bottom)
                    end.linkTo(rearLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(rearLeftBinding) {
                    bottom.linkTo(rearLeft.bottom)
                    start.linkTo(rearLeft.end)
                }
            )
        }
        with(ManySensor.Located(REAR_RIGHT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(rearRight) {
                    bottom.linkTo(tyreBox.bottom)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(rearRightStats) {
                    bottom.linkTo(rearRight.bottom)
                    start.linkTo(rearRight.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(rearRightBinding) {
                    bottom.linkTo(rearRight.bottom)
                    end.linkTo(rearRight.start)
                }
            )
        }
    }
}

@Composable
private fun SingleAxleTrailer(
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (
            carConst,
            tyreBox,
            tyreLeft,
            leftStats,
            leftBinding,
            tyreRight,
            rightStats,
            rightBinding,
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
        )
        with(ManySensor.Side(LEFT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(tyreLeft) {
                        start.linkTo(tyreBox.start)
                        top.linkTo(tyreBox.top)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(leftStats) {
                    top.linkTo(tyreLeft.top)
                    bottom.linkTo(tyreLeft.bottom)
                    end.linkTo(tyreLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(leftBinding) {
                    top.linkTo(tyreLeft.top)
                    bottom.linkTo(tyreLeft.bottom)
                    start.linkTo(tyreLeft.end)
                }
            )
        }
        with(ManySensor.Side(RIGHT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(tyreRight) {
                        end.linkTo(tyreBox.end)
                        top.linkTo(tyreBox.top)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(rightStats) {
                    top.linkTo(tyreRight.top)
                    bottom.linkTo(tyreRight.bottom)
                    start.linkTo(tyreRight.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(rightBinding) {
                        top.linkTo(tyreRight.top)
                        bottom.linkTo(tyreRight.bottom)
                        end.linkTo(tyreRight.start)
                    }
            )
        }
    }
}

@Composable
private fun Motorcycle(
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (
            carConst,
            tyreBox,
            tyreFront,
            frontStats,
            frontBinding,
            tyreRear,
            rearStats,
            rearBinding,
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
        )
        with(ManySensor.Axle(FRONT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(tyreFront) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        top.linkTo(tyreBox.top)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(frontStats) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    start.linkTo(tyreFront.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(frontBinding) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    end.linkTo(tyreFront.start)
                }
            )
        }
        with(ManySensor.Axle(REAR)) {
            Tyre(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(tyreRear) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(rearStats) {
                    top.linkTo(tyreRear.top)
                    bottom.linkTo(tyreRear.bottom)
                    start.linkTo(tyreRear.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(rearBinding) {
                        top.linkTo(tyreRear.top)
                        bottom.linkTo(tyreRear.bottom)
                        end.linkTo(tyreRear.start)
                    }
            )
        }
    }
}

@Composable
private fun TadpoleThreadWheeler(
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (
            carConst,
            tyreBox,
            frontLeft,
            frontLeftStats,
            frontLeftBinding,
            frontRight,
            frontRightStats,
            frontRightBinding,
            tyreRear,
            rearStats,
            rearBinding,
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
        )
        with(ManySensor.Located(FRONT_LEFT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(frontLeft) {
                    top.linkTo(tyreBox.top)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(frontLeftStats) {
                    top.linkTo(frontLeft.top)
                    end.linkTo(frontLeft.start, 8.dp)
                    // If not, the word "bar" for "1,50 bar" is not displayed 🤷
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(frontLeftBinding) {
                    top.linkTo(frontLeft.top)
                    start.linkTo(frontLeft.end)
                }
            )
        }
        with(ManySensor.Located(FRONT_RIGHT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(frontRight) {
                    top.linkTo(tyreBox.top)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(frontRightStats) {
                    top.linkTo(frontRight.top)
                    start.linkTo(frontRight.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(frontRightBinding) {
                    top.linkTo(frontRight.top)
                    end.linkTo(frontRight.start)
                }
            )
        }
        with(ManySensor.Axle(REAR)) {
            Tyre(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(tyreRear) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(rearStats) {
                    top.linkTo(tyreRear.top)
                    bottom.linkTo(tyreRear.bottom)
                    start.linkTo(tyreRear.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(rearBinding) {
                        top.linkTo(tyreRear.top)
                        bottom.linkTo(tyreRear.bottom)
                        end.linkTo(tyreRear.start)
                    }
            )
        }
    }
}

@Composable
private fun DeltaThreeWheeler(
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (
            carConst,
            tyreBox,
            tyreFront,
            frontStats,
            frontBinding,
            rearLeft,
            rearLeftStats,
            rearLeftBinding,
            rearRight,
            rearRightStats,
            rearRightBinding
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
        )
        with(ManySensor.Axle(FRONT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier
                    .constrainAs(tyreFront) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        top.linkTo(tyreBox.top)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(frontStats) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    start.linkTo(tyreFront.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(frontBinding) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    end.linkTo(tyreFront.start)
                }
            )
        }
        with(ManySensor.Located(REAR_LEFT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(rearLeft) {
                    bottom.linkTo(tyreBox.bottom)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(rearLeftStats) {
                    bottom.linkTo(rearLeft.bottom)
                    end.linkTo(rearLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(rearLeftBinding) {
                    bottom.linkTo(rearLeft.bottom)
                    start.linkTo(rearLeft.end)
                }
            )
        }
        with(ManySensor.Located(REAR_RIGHT)) {
            Tyre(
                manySensor = this,
                modifier = Modifier.constrainAs(rearRight) {
                    bottom.linkTo(tyreBox.bottom)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                manySensor = this,
                modifier = Modifier.constrainAs(rearRightStats) {
                    bottom.linkTo(rearRight.bottom)
                    start.linkTo(rearRight.end, 8.dp)
                }
            )
            BindSensorButton(
                manySensor = this,
                modifier = Modifier.constrainAs(rearRightBinding) {
                    bottom.linkTo(rearRight.bottom)
                    end.linkTo(rearRight.start)
                }
            )
        }
    }
}

@Preview
@Composable
private fun CarPreview() {
    Vehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent()
    )
}

@Preview
@Composable
private fun SingleAxleTrailerPreview() {
    Vehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.SINGLE_AXLE_TRAILER)
            )
        )
    )
}

@Preview
@Composable
private fun MotorcyclePreview() {
    Vehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.MOTORCYCLE)
            )
        )
    )
}

@Preview
@Composable
private fun TadpoleThreadWheelerPreview() {
    Vehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.TADPOLE_THREE_WHEELER)
            )
        )
    )
}

@Preview
@Composable
private fun DeltaThreadWheelerPreview() {
    Vehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.DELTA_THREE_WHEELER)
            )
        )
    )
}