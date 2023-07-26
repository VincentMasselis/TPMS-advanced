@file:Suppress("LongMethod")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.ui.KeepScreenOn
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.RIGHT

@Composable
public fun CurrentVehicle(
    modifier: Modifier = Modifier,
) {
    Vehicle(
        component = LocalVehicleComponent.current,
        modifier = modifier
    )
}

@Composable
public fun Vehicle(
    component: VehicleComponent,
    modifier: Modifier = Modifier,
) {
    KeepScreenOn()
    when (component.vehicle.kind) {
        Kind.CAR -> Car(modifier)
        Kind.SINGLE_AXLE_TRAILER -> SingleAxleTrailer(modifier)
        Kind.MOTORCYCLE -> Motorcycle(modifier)
        Kind.TADPOLE_THREE_WHEELER -> TadpoleThreadWheeler(modifier)
        Kind.DELTA_THREE_WHEELER -> DeltaThreeWheeler(modifier)
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
        with(Location.Wheel(FRONT_LEFT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(frontLeft) {
                    top.linkTo(tyreBox.top)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontLeftStats) {
                    top.linkTo(frontLeft.top)
                    end.linkTo(frontLeft.start, 8.dp)
                    // If not, the word "bar" for "1,50 bar" is not displayed 🤷
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(frontLeftBinding) {
                    top.linkTo(frontLeft.top)
                    start.linkTo(frontLeft.end)
                }
            )
        }
        with(Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(frontRight) {
                    top.linkTo(tyreBox.top)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontRightStats) {
                    top.linkTo(frontRight.top)
                    start.linkTo(frontRight.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(frontRightBinding) {
                    top.linkTo(frontRight.top)
                    end.linkTo(frontRight.start)
                }
            )
        }
        with(Location.Wheel(REAR_LEFT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(rearLeft) {
                    bottom.linkTo(tyreBox.bottom)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearLeftStats) {
                    bottom.linkTo(rearLeft.bottom)
                    end.linkTo(rearLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(rearLeftBinding) {
                    bottom.linkTo(rearLeft.bottom)
                    start.linkTo(rearLeft.end)
                }
            )
        }
        with(Location.Wheel(REAR_RIGHT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(rearRight) {
                    bottom.linkTo(tyreBox.bottom)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearRightStats) {
                    bottom.linkTo(rearRight.bottom)
                    start.linkTo(rearRight.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
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
            askHelp,
            tyreBox,
            tyreLeft,
            leftStats,
            leftBinding,
            tyreRight,
            rightStats,
            rightBinding,
        ) = createRefs()
        BackgroundImageAskHelp(
            Modifier.constrainAs(askHelp) {
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 4.dp)
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
        with(Location.Side(LEFT)) {
            Tyre(
                location = this,
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
                location = this,
                modifier = Modifier.constrainAs(leftStats) {
                    top.linkTo(tyreLeft.top)
                    bottom.linkTo(tyreLeft.bottom)
                    end.linkTo(tyreLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(leftBinding) {
                    top.linkTo(tyreLeft.top)
                    bottom.linkTo(tyreLeft.bottom)
                    start.linkTo(tyreLeft.end)
                }
            )
        }
        with(Location.Side(RIGHT)) {
            Tyre(
                location = this,
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
                location = this,
                modifier = Modifier.constrainAs(rightStats) {
                    top.linkTo(tyreRight.top)
                    bottom.linkTo(tyreRight.bottom)
                    start.linkTo(tyreRight.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
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
            askHelp,
            tyreBox,
            tyreFront,
            frontStats,
            frontBinding,
            tyreRear,
            rearStats,
            rearBinding,
        ) = createRefs()
        BackgroundImageAskHelp(
            Modifier.constrainAs(askHelp) {
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 4.dp)
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
        with(Location.Axle(FRONT)) {
            Tyre(
                location = this,
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
                location = this,
                modifier = Modifier.constrainAs(frontStats) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    start.linkTo(tyreFront.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(frontBinding) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    end.linkTo(tyreFront.start)
                }
            )
        }
        with(Location.Axle(REAR)) {
            Tyre(
                location = this,
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
                location = this,
                modifier = Modifier.constrainAs(rearStats) {
                    top.linkTo(tyreRear.top)
                    bottom.linkTo(tyreRear.bottom)
                    start.linkTo(tyreRear.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
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
            askHelp,
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
        BackgroundImageAskHelp(
            Modifier.constrainAs(askHelp) {
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 4.dp)
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
        with(Location.Wheel(FRONT_LEFT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(frontLeft) {
                    top.linkTo(tyreBox.top)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontLeftStats) {
                    top.linkTo(frontLeft.top)
                    end.linkTo(frontLeft.start, 8.dp)
                    // If not, the word "bar" for "1,50 bar" is not displayed 🤷
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(frontLeftBinding) {
                    top.linkTo(frontLeft.top)
                    start.linkTo(frontLeft.end)
                }
            )
        }
        with(Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(frontRight) {
                    top.linkTo(tyreBox.top)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontRightStats) {
                    top.linkTo(frontRight.top)
                    start.linkTo(frontRight.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(frontRightBinding) {
                    top.linkTo(frontRight.top)
                    end.linkTo(frontRight.start)
                }
            )
        }
        with(Location.Axle(REAR)) {
            Tyre(
                location = this,
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
                location = this,
                modifier = Modifier.constrainAs(rearStats) {
                    top.linkTo(tyreRear.top)
                    bottom.linkTo(tyreRear.bottom)
                    start.linkTo(tyreRear.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
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
            askHelp,
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
        BackgroundImageAskHelp(
            Modifier.constrainAs(askHelp) {
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 4.dp)
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
        with(Location.Axle(FRONT)) {
            Tyre(
                location = this,
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
                location = this,
                modifier = Modifier.constrainAs(frontStats) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    start.linkTo(tyreFront.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(frontBinding) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    end.linkTo(tyreFront.start)
                }
            )
        }
        with(Location.Wheel(REAR_LEFT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(rearLeft) {
                    bottom.linkTo(tyreBox.bottom)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearLeftStats) {
                    bottom.linkTo(rearLeft.bottom)
                    end.linkTo(rearLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(rearLeftBinding) {
                    bottom.linkTo(rearLeft.bottom)
                    start.linkTo(rearLeft.end)
                }
            )
        }
        with(Location.Wheel(REAR_RIGHT)) {
            Tyre(
                location = this,
                modifier = Modifier.constrainAs(rearRight) {
                    bottom.linkTo(tyreBox.bottom)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearRightStats) {
                    bottom.linkTo(rearRight.bottom)
                    start.linkTo(rearRight.end, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(rearRightBinding) {
                    bottom.linkTo(rearRight.bottom)
                    end.linkTo(rearRight.start)
                }
            )
        }
    }
}

@Composable
private fun BackgroundImageAskHelp(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "This app is built by its community,\nThis screen needs a background image",
            fontWeight = FontWeight.Light,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.End,
        )
        Spacer(Modifier.width(8.dp))
        OutlinedButton(
            onClick = {
                try {
                    context.startActivity(
                        Intent(
                            ACTION_VIEW,
                            Uri.parse(
                                "https://github.com/" +
                                        "VincentMasselis/" +
                                        "TPMS-advanced/" +
                                        "issues/" +
                                        "new?" +
                                        "labels=enhancement&template=vehicle-background-image-proposal.md"
                            )
                        )
                    )
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, "No web browser found", Toast.LENGTH_LONG).show()
                }
            },
            contentPadding = PaddingValues(
                top = 4.dp,
                bottom = 4.dp,
                start = 12.dp,
                end = 12.dp
            ),
            content = {
                Text(
                    text = "Help us",
                    fontSize = 12.sp,
                )
            }
        )
    }
}
